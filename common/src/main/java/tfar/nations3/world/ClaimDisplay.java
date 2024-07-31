package tfar.nations3.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.ChunkPos;

import java.util.Objects;
import java.util.UUID;

public class ClaimDisplay implements ContainerData {

    public enum Type {
        WILDERNESS,OWNED,OWNED_BY_OTHER;
    }

    private final TownData townData;
    private final Player player;
    private final ChunkPos center;

    public ClaimDisplay(TownData townData, Player player, ChunkPos center) {
        this.townData = townData;
        this.player = player;
        this.center = center;
    }

    @Override
    public int get(int index) {
        ChunkPos pos = convert(index);
        Type type = getType(pos);
        return type.ordinal();
    }

    @Override
    public void set(int index, int value) {
        throw new UnsupportedOperationException();//will never be needed
    }

    @Override
    public int getCount() {
        return 81;
    }

    //indexes are labelled like
    // 0 | 1  | 2  | 3...
    // 9 | 10 | 11 | 12...
    // 18| 19 | 20 | 21...
    //etc

    protected ChunkPos convert(int index) {
        int x1 = index % 9 - 4;
        int z1 = index / 9 -4;
        ChunkPos pos = new ChunkPos(center.x + x1,center.z + z1);
        return pos;
    }

    public Type getType(ChunkPos pos) {
        Town town = townData.getOwnerOf(pos);
        if (town == null) return Type.WILDERNESS;
        UUID owner = town.getOwner();
        if (Objects.equals(player.getUUID(),owner)) return Type.OWNED;
        return Type.OWNED_BY_OTHER;
    }


}
