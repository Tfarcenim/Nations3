package tfar.nations3.init;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;

public class ModCreativeTabs {
    public static final MutableComponent TITLE = Component.translatable("itemGroup.nations3");
    public static final CreativeModeTab TAB = CreativeModeTab.builder(null,-1)
            .title(TITLE)
            .icon(() -> ModBlocks.CLAIMING_TABLE.asItem().getDefaultInstance())
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModBlocks.CLAIMING_TABLE);
                output.accept(ModBlocks.DEPOSIT_STATION);
            })
            .build();
}
