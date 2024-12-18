package tfar.nations3.world;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;
import tfar.nations3.platform.Services;

import java.util.*;
import java.util.stream.Collectors;

public class Nation implements ChunkOwner {

    private final TownData data;
    private UUID owner;
    private String name;
    private final List<Town> towns = new ArrayList<>();
    private long money;
    private final Set<String> invited = new HashSet<>();
    private final Set<String> allianceInvited = new HashSet<>();
    private final Set<ChunkPos> claimed = new HashSet<>();
    private final Map<UUID,Set<TownPermission>> permissions = new HashMap<>();

    private @Nullable Rebellion rebellion;

    private final Set<Nation> allied = new HashSet<>();

    public Nation(TownData data) {
        this.data = data;
    }

    public Nation(TownData data,Town capital,String name) {
        this(data);
        towns.add(capital);
        owner = capital.getOwner();
        this.name = name;
    }

    public boolean containsTown(Town town) {
        return towns.contains(town);
    }

    @Override
    public Set<ChunkPos> getClaimed() {
        return claimed;
    }

    public void deposit(long amount) {
        money+= amount;
        setDirty();
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public boolean addTown(Town town) {
        towns.add(town);
        setDirty();
        return true;
    }

    public boolean isAllied(Nation other) {
        return allied.contains(other);
    }

    @Override
    public boolean containsCitizen(UUID uuid) {
        return towns.stream().anyMatch(town -> town.containsCitizen(uuid));
    }

    @Override
    public Set<UUID> getAllCitizens() {
        Set<UUID> set = towns.stream().flatMap(town -> town.getAllCitizens().stream()).collect(Collectors.toSet());
        return set;
    }

    public void addAlliance(Nation other) {
        allied.add(other);
        setDirty();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        setDirty();
    }

    public boolean removeTown(Town town) {
        boolean remove = towns.remove(town);
        if (remove) {
            setDirty();
        }
        return remove;
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setMoney(long money) {
        this.money = money;
        setDirty();
    }

    @Override
    public long getMoney() {
        return money;
    }

    public List<Town> getTowns() {
        return towns;
    }

    @Override
    public void setDirty() {
        data.setDirty();
    }

    @Override
    public TownData getData() {
        return data;
    }

    public void deepUnclaim(Set<ChunkPos> chunkPos) {
        claimed.removeIf(chunkPos::contains);
        for (Town town : towns) {
            town.getClaimed().removeIf(chunkPos::contains);
        }
        setDirty();
    }

    public void addInvite(String name) {
        invited.add(name);
    }


    public boolean hasInvite(String name) {
        return invited.contains(name);
    }

    public void removeInvite(String name) {
        invited.remove(name);
    }

    public boolean addAllianceInvite(String name) {
        return allianceInvited.add(name);
    }

    public boolean hasAllianceInvite(String name) {
        return allianceInvited.contains(name);
    }

    public void removeAllianceInvite(String name) {
        allianceInvited.remove(name);
    }

    public void setRebellion(Rebellion rebellion) {
        this.rebellion = rebellion;
    }

    public Rebellion getRebellion() {
        return rebellion;
    }

    public void grantPermission(UUID uuid,TownPermission townPermission) {
            permissions.computeIfAbsent(uuid, k -> new HashSet<>());

            permissions.get(uuid).add(townPermission);
            setDirty();
    }

    public void revokePermission(UUID uuid,TownPermission townPermission) {
        if (permissions.containsKey(uuid)) {
            permissions.get(uuid).remove(townPermission);
            setDirty();
        }
    }

    public boolean checkPermission(UUID uuid,TownPermission permission) {
        if (isOwner(uuid)) return true;
        return permissions.containsKey(uuid) && permissions.get(uuid).contains(permission);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", owner);
        tag.putString("name",name);
        tag.put("towns", saveTowns());
        tag.putLong("money",money);
        tag.put("allied",saveNations(allied));
        tag.put("claimed", saveClaimed());
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

    public ListTag saveNations(Set<Nation> nations) {
        ListTag townTag = new ListTag();
        for (Nation nation : nations) {
            townTag.add(StringTag.valueOf(nation.getName()));
        }
        return townTag;
    }

    public ListTag saveTowns() {
        ListTag townTag = new ListTag();
        for (Town town : towns) {
            townTag.add(StringTag.valueOf(town.getName()));
        }
        return townTag;
    }

    public void load(CompoundTag tag) {
        owner = tag.getUUID("owner");
        name = tag.getString("name");
        ListTag townTag = tag.getList("towns", Tag.TAG_STRING);
        for (Tag tag1 : townTag) {
            StringTag stringTag = (StringTag) tag1;
            Town town = data.getTownByName(stringTag.getAsString());
            towns.add(town);
        }
        loadNations(allied,tag.getList("allied",Tag.TAG_STRING));
        money = tag.getLong("money");
        ListTag claimedTag = tag.getList("claimed", Tag.TAG_COMPOUND);
        for (Tag tag1 : claimedTag) {
            CompoundTag compoundTag = (CompoundTag) tag1;
            claimed.add(new ChunkPos(compoundTag.getInt("x"), compoundTag.getInt("z")));
        }
    }


    public void loadNations(Set<Nation> nations,ListTag listTag) {
        for (Tag tag1 : listTag) {
            StringTag stringTag = (StringTag) tag1;
            Nation nation = data.getNationByName(stringTag.getAsString());
            nations.add(nation);
        }
    }

     public List<Component> buildNationInfo() {
        List<Component> list = new ArrayList<>();
        list.add(Component.literal("Nation Info").withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.literal("Name: " + name));
        list.add(Component.literal("Owner: " + Services.PLATFORM.getLastKnownUserName(owner)));
        list.add(Component.literal("Nation Money: " + money));
        list.add(Component.literal("Towns").withStyle(ChatFormatting.UNDERLINE));
        for (Town town : getTowns()) {
            list.add(Component.literal("Town: " + town.getName()));
        }
        return list;
    }
}
