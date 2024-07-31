package tfar.nations3.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import tfar.nations3.Nations3;
import tfar.nations3.menu.ClaimingTableMenu;

import javax.annotation.Nullable;

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
        boolean $$7 = true;
        boolean $$8 = false;
        boolean $$9 = false;
        ItemStack id = this.menu.getSlot(0).getItem();
        boolean $$11 = false;
        Integer $$14;
        MapItemSavedData $$13;
        if (id.is(Items.FILLED_MAP)) {
            $$14 = MapItem.getMapId(id);
            $$13 = MapItem.getSavedData($$14, this.minecraft.level);
            if ($$13 != null) {
                if ($$13.locked) {
                    $$11 = true;
                }

            }
        } else {
            $$14 = null;
            $$13 = null;
        }

        this.renderResultingMap($$0, $$14, $$13, $$7, $$8, $$9, $$11);
    }

    private void renderResultingMap(GuiGraphics $$0, @Nullable Integer $$1, @Nullable MapItemSavedData $$2, boolean $$3, boolean $$4, boolean $$5, boolean $$6) {
        int $$7 = this.leftPos;
        int $$8 = this.topPos;
        if ($$4 && !$$6) {
            $$0.blit(BACKGROUND, $$7 + 67, $$8 + 13, this.imageWidth, 66, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 85, $$8 + 31, 0.226F);
        } else if ($$3) {
            $$0.blit(BACKGROUND, $$7 + 67 + 16, $$8 + 13, this.imageWidth, 132, 50, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 86, $$8 + 16, 0.34F);
            $$0.pose().pushPose();
            $$0.pose().translate(0.0F, 0.0F, 1.0F);
            $$0.blit(BACKGROUND, $$7 + 67, $$8 + 13 + 16, this.imageWidth, 132, 50, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 70, $$8 + 32, 0.34F);
            $$0.pose().popPose();
        } else if ($$5) {
            $$0.blit(BACKGROUND, $$7 + 67, $$8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 71, $$8 + 17, 0.45F);
            $$0.pose().pushPose();
            $$0.pose().translate(0.0F, 0.0F, 1.0F);
            $$0.blit(BACKGROUND, $$7 + 66, $$8 + 12, 0, this.imageHeight, 66, 66);
            $$0.pose().popPose();
        } else {
            $$0.blit(BACKGROUND, $$7 + 67, $$8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 71, $$8 + 17, 0.45F);
        }

    }

    private void renderMap(GuiGraphics $$0, @Nullable Integer $$1, @Nullable MapItemSavedData $$2, int $$3, int $$4, float $$5) {
        if ($$1 != null && $$2 != null) {
            $$0.pose().pushPose();
            $$0.pose().translate((float) $$3, (float) $$4, 1.0F);
            $$0.pose().scale($$5, $$5, 1.0F);
            this.minecraft.gameRenderer.getMapRenderer().render($$0.pose(), $$0.bufferSource(), $$1, $$2, true, 15728880);
            $$0.flush();
            $$0.pose().popPose();
        }

    }
}
