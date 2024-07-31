package tfar.nations3.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tfar.nations3.init.ModBlocks;
import tfar.nations3.init.ModMenuTypes;

public class ClaimingTableMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;
    private final Container container = new SimpleContainer(1);

    public ClaimingTableMenu(int id, Inventory inventory) {
        this(id,inventory,ContainerLevelAccess.NULL);
    }

    public ClaimingTableMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.CLAIMING_TABLE, id);
        this.access = access;

        /*addSlot(new Slot(container,0,0,0){
            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.getItem() instanceof MapItem;
            }
        });*/

        int startY = 77;

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + startY));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142 + startY));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.CLAIMING_TABLE);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> {
            this.clearContainer(player, container);
        });
    }
}
