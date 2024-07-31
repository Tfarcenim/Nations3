package tfar.nations3.network;

import net.minecraft.server.level.ServerPlayer;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
