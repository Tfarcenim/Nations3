package tfar.nations3.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.nations3.datagen.data.ModBlockTagsProvider;
import tfar.nations3.datagen.data.ModLootTableProvider;
import tfar.nations3.datagen.data.ModRecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModDatagen {

    public static void gather(GatherDataEvent event) {
        boolean client = event.includeClient();
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        dataGenerator.addProvider(client,new ModLangProvider(packOutput));
        dataGenerator.addProvider(client,new ModBlockstateProvider(packOutput,existingFileHelper));
        if (event.includeServer()) {
            dataGenerator.addProvider(true, ModLootTableProvider.create(packOutput));
            dataGenerator.addProvider(true, new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper));
            dataGenerator.addProvider(true, new ModRecipeProvider(packOutput));
        }

    }
}
