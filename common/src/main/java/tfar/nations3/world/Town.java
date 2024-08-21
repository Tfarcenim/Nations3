package tfar.nations3.world;

import net.minecraft.nbt.*;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

public class Town {

    private final TownData townData;
    private UUID owner;
    private String name;
    private final Map<UUID,Set<TownPermission>> citizens = new HashMap<>();
    private final Set<ChunkPos> claimed = new HashSet<>();
    private long money;

    public Town(TownData townData) {
        this.townData = townData;
    }

    public Town(TownData townData, UUID owner, String name) {
        this(townData);
        this.owner = owner;
        this.name = name;
        setOwner(owner);
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
        citizens.put(uuid,new HashSet<>());
        setDirty();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        citizens.put(owner,new HashSet<>(TownPermissions.getAllPermissions()));
        setDirty();
    }

    public void grantPermission(UUID uuid,TownPermission townPermission) {
        if (citizens.containsKey(uuid)) {
            Set<TownPermission> permissions = citizens.computeIfAbsent(uuid,uuid1 -> new HashSet<>());
            permissions.add(townPermission);
            setDirty();
        }
    }

    public void revokePermission(UUID uuid,TownPermission townPermission) {
        if (citizens.containsKey(uuid)) {
            Set<TownPermission> permissions = citizens.get(uuid);
            if (permissions == null) return;
            permissions.remove(townPermission);
            setDirty();
        }
    }

    public boolean checkPermission(UUID uuid,TownPermission permission) {
        return citizens.containsKey(uuid) && citizens.get(uuid).contains(permission);
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

    public Set<TownPermission> getPermissions(UUID uuid) {
        if (citizens.containsKey(uuid)) {
            return citizens.get(uuid);
        }
        return Set.of();
    }

    public boolean hasClaim(ChunkPos pos) {
        return claimed.contains(pos);
    }

    public long getMoney() {
        return money;
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
        for (Map.Entry<UUID,Set<TownPermission>> entry : citizens.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("uuid",entry.getKey());
            ListTag permissionTag = new ListTag();
            for (TownPermission townPermission : entry.getValue()) {
                permissionTag.add(StringTag.valueOf(townPermission.key()));
            }
            tag.put("permissions",permissionTag);
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
    }

    public void loadCitizens(ListTag listTag) {
        for (Tag tag1 : listTag) {
            CompoundTag compoundTag = (CompoundTag) tag1;
            UUID uuid = compoundTag.getUUID("uuid");
            ListTag permissionTag = compoundTag.getList("permissions",Tag.TAG_STRING);
            Set<TownPermission> permissions = new HashSet<>();
            for (Tag tag : permissionTag) {
                String string = tag.getAsString();
                TownPermission permission = TownPermissions.getPermission(string);
                permissions.add(permission);
            }
            citizens.put(uuid,permissions);
        }
    }
}
