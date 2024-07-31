package tfar.nations3.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import tfar.nations3.init.ModBlocks;
import tfar.nations3.init.ModMenuTypes;

public class ClaimingTableMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;

    public ClaimingTableMenu(int id, Inventory inventory) {
        this(id,inventory,ContainerLevelAccess.NULL);
    }

    public ClaimingTableMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.CLAIMING_TABLE, id);
        this.access = access;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.CLAIMING_TABLE);
    }
}
