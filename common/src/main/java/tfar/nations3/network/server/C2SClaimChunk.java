package tfar.nations3.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import tfar.nations3.world.Town;
import tfar.nations3.world.TownData;

public class C2SClaimChunk implements C2SModPacket {

    public final int x;
    public final int z;
    public final boolean remove;

    public C2SClaimChunk(FriendlyByteBuf buf) {
        x = buf.readInt();
        z = buf.readInt();
        remove = buf.readBoolean();
    }

    public C2SClaimChunk(int x, int z,boolean remove) {
        this.x = x;
        this.z = z;
        this.remove = remove;
    }

    @Override
    public void handleServer(ServerPlayer player) {
        ChunkPos playerPos = new ChunkPos(player.blockPosition());
        ChunkPos chunkPos = new ChunkPos(playerPos.x+x,playerPos.z+z);
        TownData townData = TownData.getInstance(player.serverLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null) {
                if (remove) {
                    town.unClaim(chunkPos);
                } else {
                    town.claim(chunkPos);
                }
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(x);
        to.writeInt(z);
        to.writeBoolean(remove);
    }
}
