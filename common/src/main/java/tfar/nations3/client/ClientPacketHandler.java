package tfar.nations3.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import tfar.nations3.menu.ClaimingTableMenu;
import tfar.nations3.network.client.S2CTownInfoPacket;

public class ClientPacketHandler {

    public static void handleTownInfoPacket(S2CTownInfoPacket s2CTownInfoPacket) {
        Player player = Minecraft.getInstance().player;
        if (player.containerMenu instanceof ClaimingTableMenu claimingTableMenu && claimingTableMenu.containerId == s2CTownInfoPacket.containerId) {
            claimingTableMenu.setTownInfo(s2CTownInfoPacket.index, s2CTownInfoPacket.value);
        }
    }
}
