package tfar.nations3.datagen.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(PackOutput pOutput, Set<ResourceLocation> pRequiredTables, List<SubProviderEntry> pSubProviders) {
        super(pOutput, pRequiredTables, pSubProviders);
    }

    public static LootTableProvider create(PackOutput pOutput) {
        return new ModLootTableProvider(pOutput, BuiltInLootTables.all(), List.of(new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
    }
}
