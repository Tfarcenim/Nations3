package tfar.nations3.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import tfar.nations3.Nations3;
import tfar.nations3.menu.ClaimingTableMenu;

import javax.annotation.Nullable;

public class ClaimingTableScreen extends AbstractContainerScreen<ClaimingTableMenu> {

    protected static final ResourceLocation BACKGROUND = Nations3.id("textures/gui/claiming_table.png");

    public ClaimingTableScreen(ClaimingTableMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        imageHeight +=78;
        inventoryLabelY += 78;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int $$4 = this.leftPos;
        int $$5 = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(BACKGROUND, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        //MapItemSavedData mapItemSavedData;
        //mapItemSavedData = MapItem.getSavedData(0, this.minecraft.level);

        //this.renderResultingMap($$0, 0, mapItemSavedData);
        renderGrid(pGuiGraphics);
    }

    private void renderGrid(GuiGraphics pGuiGraphics) {
        int yStart = topPos + 18;
        int xStart = leftPos +15;
        int size = 14;
        int length = size * 9;
        for (int i = 0; i < 10;i++) {
            pGuiGraphics.hLine(xStart,xStart + length,yStart + i * size,0xffffffff);
            pGuiGraphics.vLine(xStart + i * size,yStart,yStart + length,0xffffffff);
        }
    }

    private void renderResultingMap(GuiGraphics $$0, @Nullable Integer $$1, @Nullable MapItemSavedData $$2) {
        int $$7 = this.leftPos;
        int $$8 = this.topPos;
        $$0.blit(BACKGROUND, $$7 + 8, $$8 + 13, this.imageWidth, 132, 50, 66);
        this.renderMap($$0, $$1, $$2, $$7 + 8, $$8 + 16, 1);
    }

    private void renderMap(GuiGraphics $$0, @Nullable Integer $$1, @Nullable MapItemSavedData $$2, int $$3, int $$4, float scale) {
        if ($$1 != null && $$2 != null) {
            $$0.pose().pushPose();
            $$0.pose().translate((float) $$3, (float) $$4, 1.0F);
            $$0.pose().scale(scale, scale, 1.0F);
            this.minecraft.gameRenderer.getMapRenderer().render($$0.pose(), $$0.bufferSource(), $$1, $$2, true, 15728880);
            $$0.flush();
            $$0.pose().popPose();
        }

    }
}
