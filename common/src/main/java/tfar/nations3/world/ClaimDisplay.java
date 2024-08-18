package tfar.nations3.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

import java.util.Objects;

public class ClaimDisplay implements TownInfos {

    private final TownData townData;
    private final Player player;
    private final ChunkPos center;

    public ClaimDisplay(TownData townData, Player player, ChunkPos center) {
        this.townData = townData;
        this.player = player;
        this.center = center;
    }

    @Override
    public TownInfo get(int index) {
        ChunkPos pos = convert(index);
        return getInfo(pos);
    }

    @Override
    public void set(int index, TownInfo value) {
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

    public TownInfo getInfo(ChunkPos pos) {
        Town townAtChunk = townData.getOwnerOf(pos);

        if (townAtChunk == null) return TownInfo.WILDERNESS;

        Town playerTown = townData.getTownByPlayer(player.getUUID());

        if (Objects.equals(townAtChunk,playerTown)) {
            return new TownInfo(townAtChunk.getName(),Relations.OWN,false);
        }

        return new TownInfo(townAtChunk.getName(),Relations.NEUTRAL,false);
    }
}
