package tfar.nations3.network;

import net.minecraft.resources.ResourceLocation;
import tfar.nations3.Nations3;
import tfar.nations3.network.client.S2CTownInfoPacket;
import tfar.nations3.network.server.C2SClaimChunk;
import tfar.nations3.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        Services.PLATFORM.registerServerPacket(C2SClaimChunk.class, C2SClaimChunk::new);
        Services.PLATFORM.registerClientPacket(S2CTownInfoPacket.class, S2CTownInfoPacket::new);

    }

    public static ResourceLocation packet(Class<?> clazz) {
        return Nations3.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
