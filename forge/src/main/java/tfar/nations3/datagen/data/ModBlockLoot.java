package tfar.nations3.datagen.data;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import tfar.nations3.Nations3;
import tfar.nations3.init.ModBlocks;

public class ModBlockLoot extends VanillaBlockLoot {

    @Override
    protected void generate() {
        dropSelf(ModBlocks.CLAIMING_TABLE);
        dropSelf(ModBlocks.DEPOSIT_STATION);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Nations3.getKnownBlocks().toList();
    }
}
