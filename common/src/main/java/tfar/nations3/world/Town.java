package tfar.nations3.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

public class Town {

    private final TownData townData;
    private UUID owner;
    private String name;
    private List<UUID> citizens = new ArrayList<>();
    private Set<ChunkPos> claimed = new HashSet<>();
    private long money;

    public Town(TownData townData) {
        this.townData = townData;
    }

    public Town(TownData townData, UUID owner, String name) {
        this(townData);
        this.owner = owner;
        this.name = name;
        citizens.add(owner);
        townData.setDirty();
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

    public UUID getOwner() {
        return owner;
    }

    public boolean containsPlayer(UUID uuid) {
        return citizens.contains(uuid);
    }

    public List<UUID> getCitizens() {
        return citizens;
    }

    public boolean hasClaim(ChunkPos pos) {
        return claimed.contains(pos);
    }

    public long getMoney() {
        return money;
    }
    public void deposit(long amount) {
        money+=amount;
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
        tag.putString("name",name);
        tag.putLong("money",money);
        tag.put("claimed", saveClaimed());
        tag.put("citizens",saveCitizens());
        return tag;
    }

    public ListTag saveClaimed() {
        ListTag claimedTag = new ListTag();
        for (ChunkPos chunkPos : claimed) {
            CompoundTag chunkPosTag = new CompoundTag();
            chunkPosTag.putInt("x",chunkPos.x);
            chunkPosTag.putInt("z",chunkPos.z);
            claimedTag.add(chunkPosTag);
        }
        return claimedTag;
    }

    //uuids are saved as IntArrayTags
    public ListTag saveCitizens() {
        ListTag citizenTag = new ListTag();
        for (UUID uuid : citizens) {
            citizenTag.add(NbtUtils.createUUID(uuid));
        }
        return citizenTag;
    }

    public void load(CompoundTag tag) {
        owner = tag.getUUID("owner");
        name = tag.getString("name");
        money = tag.getLong("money");
        ListTag claimedTag = tag.getList("claimed",Tag.TAG_COMPOUND);
        for (Tag tag1 : claimedTag) {
            CompoundTag compoundTag = (CompoundTag) tag1;
            claimed.add(new ChunkPos(compoundTag.getInt("x"),compoundTag.getInt("z")));
        }

        ListTag citizenTag = tag.getList("citizens",Tag.TAG_INT_ARRAY);
        for (Tag tag1 : citizenTag) {
            citizens.add(NbtUtils.loadUUID(tag1));
        }
    }
}
