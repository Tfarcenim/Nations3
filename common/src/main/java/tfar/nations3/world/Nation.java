package tfar.nations3.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public class Nation {

    private final TownData data;
    private UUID owner;
    private String name;
    private final List<Town> towns = new ArrayList<>();
    private long money;
    private final Set<String> invited = new HashSet<>();
    private final Set<String> allianceInvited = new HashSet<>();

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

    public void addAlliance(Nation other) {
        allied.add(other);
        setDirty();
    }

    public String getName() {
        return name;
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

    public long getMoney() {
        return money;
    }

    public List<Town> getTowns() {
        return towns;
    }

    public void setDirty() {
        data.setDirty();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", owner);
        tag.putString("name",name);
        tag.put("towns", saveTowns());
        tag.putLong("money",money);
        tag.put("allied",saveNations(allied));
        return tag;
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
    }

    public void loadNations(Set<Nation> nations,ListTag listTag) {
        for (Tag tag1 : listTag) {
            StringTag stringTag = (StringTag) tag1;
            Nation nation = data.getNationByName(stringTag.getAsString());
            nations.add(nation);
        }
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

}
