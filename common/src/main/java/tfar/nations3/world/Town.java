package tfar.nations3.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Town {

    private final TownData townData;
    private UUID owner;
    private String name;
    private Set<ChunkPos> claimed = new HashSet<>();

    public Town(TownData townData){
        this.townData = townData;
    }

    public Town(TownData townData, UUID owner, String name) {
        this(townData);
        this.owner = owner;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDirty() {
        townData.setDirty();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", owner);
        tag.putString(name,"name");
        ListTag claimedTag = new ListTag();
        for (ChunkPos chunkPos : claimed) {
            CompoundTag chunkPosTag = new CompoundTag();
            chunkPosTag.putInt("x",chunkPos.x);
            chunkPosTag.putInt("z",chunkPos.z);
            claimedTag.add(chunkPosTag);
        }
        tag.put("claimed", claimedTag);
        return tag;
    }

    public void load(CompoundTag tag) {
        owner = tag.getUUID("owner");
        name = tag.getString("name");
        ListTag claimedTag = tag.getList("claimed",Tag.TAG_COMPOUND);
        for (Tag tag1 : claimedTag) {
            CompoundTag compoundTag = (CompoundTag) tag1;
            claimed.add(new ChunkPos(compoundTag.getInt("x"),compoundTag.getInt("z")));
        }
    }

}
