package tfar.nations3.datagen.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.nations3.Nations3;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput generatorIn, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn,lookupProvider, Nations3.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
      //  tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.MINEABLE_WATER,ModBlocks.MINEABLE_LAVA);
      //  tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.REINFORCED_DEEPSLATE,Blocks.BEDROCK,ModBlocks.MINEABLE_WATER,ModBlocks.MINEABLE_LAVA);
    }
}
