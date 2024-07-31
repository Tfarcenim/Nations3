package tfar.nations3.init;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.nations3.menu.ClaimingTableMenu;

public class ModMenuTypes {
    public static final MenuType<ClaimingTableMenu> CLAIMING_TABLE = new MenuType<>(ClaimingTableMenu::new, FeatureFlags.VANILLA_SET);
}
