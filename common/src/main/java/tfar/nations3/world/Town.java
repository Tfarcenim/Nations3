package tfar.nations3.world;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;
import tfar.nations3.platform.Services;

import java.util.*;

public class Town {

    private final TownData townData;
    private UUID owner;
    private String name;
    private final Map<UUID,CitizenInfo> citizens = new HashMap<>();
    private final Set<ChunkPos> claimed = new HashSet<>();
    private long money;
    private long taxRate;
    private final Set<UUID> invited = new HashSet<>();

    public Town(TownData townData) {
        this.townData = townData;
    }

    public Town(TownData townData, UUID owner, String name) {
        this(townData);
        this.owner = owner;
        this.name = name;
        setOwner(owner);
    }

    public void setTaxRate(long taxRate) {
        this.taxRate = taxRate;
    }

    public String getName() {
        return name;
    }

    public Set<ChunkPos> getClaimed() {
        return claimed;
    }

    public void clearClaimed() {
        claimed.clear();
        setDirty();
    }

    public void addCitizen(UUID uuid) {
        citizens.put(uuid,new CitizenInfo());
        setDirty();
    }

    public void removeCitizen(UUID uuid) {
        citizens.remove(uuid);
        setDirty();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        citizens.put(owner,new CitizenInfo(0,new HashSet<>(TownPermissions.getAllPermissions())));
        setDirty();
    }

    public void grantPermission(UUID uuid,TownPermission townPermission) {
        if (citizens.containsKey(uuid)) {
            CitizenInfo citizeninfo = citizens.get(uuid);
            Set<TownPermission> permissions = citizeninfo.permissions;
            permissions.add(townPermission);
            setDirty();
        }
    }

    public void revokePermission(UUID uuid,TownPermission townPermission) {
        if (citizens.containsKey(uuid)) {
            CitizenInfo citizeninfo = citizens.get(uuid);
            Set<TownPermission> permissions = citizeninfo.permissions;
            if (permissions == null) return;
            permissions.remove(townPermission);
            setDirty();
        }
    }

    public boolean checkPermission(UUID uuid,TownPermission permission) {
        if (isOwner(uuid)) return true;
        return citizens.containsKey(uuid) && citizens.get(uuid).permissions.contains(permission);
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isOwner(UUID uuid) {
        return Objects.equals(uuid,owner);
    }

    public boolean containsCitizen(UUID uuid) {
        return citizens.containsKey(uuid);
    }

    public Set<UUID> getCitizens() {
        return citizens.keySet();
    }

    @Nullable
    public CitizenInfo getInfo(UUID uuid) {
        return citizens.get(uuid);
    }

    public Set<TownPermission> getPermissions(UUID uuid) {
        if (citizens.containsKey(uuid)) {
            return citizens.get(uuid).permissions;
        }
        return Set.of();
    }

    public boolean hasClaim(ChunkPos pos) {
        return claimed.contains(pos);
    }

    public long getMoney() {
        return money;
    }

    public void personalDeposit(UUID uuid,long amount) {
        CitizenInfo citizenInfo = citizens.get(uuid);
        if (citizenInfo != null) {
            citizenInfo.money +=amount;
            setDirty();
        }
    }

    public void deposit(long amount) {
        money += amount;
        setDirty();
    }

    public boolean claim(ChunkPos pos) {
        boolean add = claimed.add(pos);
        if (add) setDirty();
        return add;
    }

    public boolean unClaim(ChunkPos pos) {
        boolean remove = claimed.remove(pos);
        if (remove) setDirty();
        return remove;
    }

    public void collectTaxes() {
        for (Map.Entry<UUID,CitizenInfo> entry:citizens.entrySet()) {
            UUID uuid = entry.getKey();
            CitizenInfo citizenInfo = entry.getValue();
            if (taxRate > citizenInfo.money) {
                money+= citizenInfo.money;
                citizenInfo.money = 0;
                ServerPlayer owner = townData.level.getServer().getPlayerList().getPlayer(getOwner());
                if (owner != null) {
                    String name = Services.PLATFORM.getLastKnownUserName(uuid);
                    owner.sendSystemMessage(Component.literal(name+ " has not paid all of their taxes"));
                }
            } else {
                money+=taxRate;
                citizenInfo.money-=taxRate;
            }
        }
        setDirty();
    }


    public void setDirty() {
        townData.setDirty();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", owner);
        tag.putString("name", name);
        tag.putLong("money", money);
        tag.put("claimed", saveClaimed());
        tag.put("citizens", saveCitizens());
        tag.putLong("tax_rate",taxRate);
        return tag;
    }

    public ListTag saveClaimed() {
        ListTag claimedTag = new ListTag();
        for (ChunkPos chunkPos : claimed) {
            CompoundTag chunkPosTag = new CompoundTag();
            chunkPosTag.putInt("x", chunkPos.x);
            chunkPosTag.putInt("z", chunkPos.z);
            claimedTag.add(chunkPosTag);
        }
        return claimedTag;
    }

    //uuids are saved as IntArrayTags
    public ListTag saveCitizens() {
        ListTag listTag = new ListTag();
        for (Map.Entry<UUID,CitizenInfo> entry : citizens.entrySet()) {
            CompoundTag tag = entry.getValue().toTag();
            tag.putUUID("uuid",entry.getKey());
            listTag.add(tag);
        }
        return listTag;
    }

    public void load(CompoundTag tag) {
        owner = tag.getUUID("owner");
        name = tag.getString("name");
        money = tag.getLong("money");
        ListTag claimedTag = tag.getList("claimed", Tag.TAG_COMPOUND);
        for (Tag tag1 : claimedTag) {
            CompoundTag compoundTag = (CompoundTag) tag1;
            claimed.add(new ChunkPos(compoundTag.getInt("x"), compoundTag.getInt("z")));
        }
        loadCitizens(tag.getList("citizens", Tag.TAG_COMPOUND));
        taxRate = tag.getLong("tax_rate");
    }

    public void loadCitizens(ListTag listTag) {
        for (Tag tag1 : listTag) {
            CompoundTag compoundTag = (CompoundTag) tag1;
            UUID uuid = compoundTag.getUUID("uuid");
            CitizenInfo citizenInfo = CitizenInfo.fromTag(compoundTag);
            citizens.put(uuid,citizenInfo);
        }
    }

    public long getTaxRate() {
        return taxRate;
    }

    public void addInvite(UUID uuid) {
        invited.add(uuid);
    }

    public boolean hasInvite(UUID uuid) {
        return invited.contains(uuid);
    }

    public void removeInvite(UUID uuid) {
        invited.remove(uuid);
    }

}
