package tfar.nations3.commands;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerLevel;
import tfar.nations3.world.TownData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Suggestions {
    protected static final SuggestionProvider<CommandSourceStack> ALL_TOWNS = (commandContext, suggestionsBuilder) -> {
        ServerLevel serverLevel = commandContext.getSource().getLevel();
        TownData townData = TownData.getInstance(serverLevel);
        if (townData != null) {
            Set<String> collection = townData.getTownsByName().keySet();
            return SharedSuggestionProvider.suggest(collection,suggestionsBuilder);
        } else {
            return SharedSuggestionProvider.suggest(List.of(),suggestionsBuilder);
        }
    };
    protected static final SuggestionProvider<CommandSourceStack> ALL_NATIONS = (commandContext, suggestionsBuilder) -> {
        ServerLevel serverLevel = commandContext.getSource().getLevel();
        TownData townData = TownData.getInstance(serverLevel);
        if (townData != null) {
            Set<String> collection = townData.getNationsByName().keySet();
            return SharedSuggestionProvider.suggest(collection,suggestionsBuilder);
        } else {
            return SharedSuggestionProvider.suggest(List.of(),suggestionsBuilder);
        }
    };
}
