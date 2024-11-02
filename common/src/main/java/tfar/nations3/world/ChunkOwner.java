package tfar.nations3.world;

import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public interface ChunkOwner {
    Set<ChunkPos> getClaimed();
    default boolean hasClaim(ChunkPos pos) {
        return getClaimed().contains(pos);
    }

    default boolean claim(ChunkPos pos) {
        boolean add = getClaimed().add(pos);
        if (add) setDirty();
        return add;
    }

    default boolean unClaim(ChunkPos pos) {
        boolean remove = getClaimed().remove(pos);
        if (remove) setDirty();
        return remove;
    }

    void setDirty();

    String getName();
}
