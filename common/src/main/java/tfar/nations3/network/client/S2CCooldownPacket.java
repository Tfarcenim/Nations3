package tfar.nations3.network.client;

import net.minecraft.network.FriendlyByteBuf;
import tfar.nations3.client.ClientPacketHandler;

public class S2CCooldownPacket implements S2CModPacket {

    public int[] cooldowns;

    public S2CCooldownPacket(int[]cooldowns){
        this.cooldowns = cooldowns;
    }

    public S2CCooldownPacket(FriendlyByteBuf buf) {

    }

    @Override
    public void handleClient() {
        ClientPacketHandler.handle();
    }

    @Override
    public void write(FriendlyByteBuf to) {

    }
}
