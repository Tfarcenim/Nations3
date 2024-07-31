package tfar.nations3.network;

import net.minecraft.resources.ResourceLocation;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.platform.Services;
import tfar.nations3.Nations3;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        Services.PLATFORM.registerClientPacket(S2CCooldownPacket.class, S2CCooldownPacket::new);

    }

    public static ResourceLocation packet(Class<?> clazz) {
        return Nations3.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
