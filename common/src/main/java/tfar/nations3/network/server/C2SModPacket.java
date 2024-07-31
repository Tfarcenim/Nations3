package tfar.nations3.network.server;

import net.minecraft.server.level.ServerPlayer;
import tfar.nations3.network.ModPacket;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
