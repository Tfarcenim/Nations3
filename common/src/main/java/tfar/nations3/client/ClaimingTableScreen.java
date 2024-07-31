package tfar.nations3.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.nations3.Nations3;
import tfar.nations3.menu.ClaimingTableMenu;

public class ClaimingTableScreen extends AbstractContainerScreen<ClaimingTableMenu> {

    protected static final ResourceLocation BACKGROUND = Nations3.id("textures/gui/claiming_table.png");

    public ClaimingTableScreen(ClaimingTableMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = this.leftPos;
        int $$5 = (this.height - this.imageHeight) / 2;
        $$0.blit(BACKGROUND, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
    }

}
