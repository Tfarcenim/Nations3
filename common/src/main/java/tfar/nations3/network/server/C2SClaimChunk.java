package tfar.nations3.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.ChunkPos;
import tfar.nations3.menu.ClaimingTableMenu;
import tfar.nations3.world.Town;
import tfar.nations3.world.TownData;
import tfar.nations3.world.TownPermissions;

public class C2SClaimChunk implements C2SModPacket {

    public final int x;
    public final int z;
    public final boolean remove;

    public C2SClaimChunk(FriendlyByteBuf buf) {
        x = buf.readInt();
        z = buf.readInt();
        remove = buf.readBoolean();
    }

    public C2SClaimChunk(int x, int z, boolean remove) {
        this.x = x;
        this.z = z;
        this.remove = remove;
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (player.containerMenu instanceof ClaimingTableMenu claimingTableMenu) {
            ContainerLevelAccess access = claimingTableMenu.getAccess();
            ChunkPos playerPos = new ChunkPos(access.evaluate((level, pos) -> pos).orElseThrow());
            ChunkPos chunkPos = new ChunkPos(playerPos.x + x, playerPos.z + z);
            TownData townData = TownData.getInstance(player.serverLevel());
            if (townData != null) {
                Town town = townData.getTownByPlayer(player.getUUID());
                if (town != null && town.checkPermission(player.getUUID(), TownPermissions.MANAGE_CLAIMS)) {
                    if (remove) {
                        town.unClaim(chunkPos);
                    } else {
                        town.claim(chunkPos);
                    }
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
