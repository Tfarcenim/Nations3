package tfar.nations3.client;

import net.minecraft.client.gui.screens.MenuScreens;
import tfar.nations3.init.ModMenuTypes;

public class ModClient {

    public static void init() {
        MenuScreens.register(ModMenuTypes.CLAIMING_TABLE,ClaimingTableScreen::new);
    }



}
