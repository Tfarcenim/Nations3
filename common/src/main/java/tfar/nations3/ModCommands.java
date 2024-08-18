package tfar.nations3;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import tfar.nations3.platform.Services;
import tfar.nations3.world.Town;
import tfar.nations3.world.TownData;

import java.util.*;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("town")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ModCommands::createTown)
                        )
                )
                .then(Commands.literal("clear_claims")
                        .executes(ModCommands::removeAllOwnClaims)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .suggests(ALL_TOWNS)
                                .executes(ModCommands::removeAllClaims)
                        )
                        .then(Commands.literal("all")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(ModCommands::removeAllAllClaims)
                        )
                )
                .then(Commands.literal("destroy")
                        .executes(ModCommands::destroyOwnTown)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .suggests(ALL_TOWNS)
                                .executes(ModCommands::destroyTown)
                        )
                        .then(Commands.literal("all")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(ModCommands::destroyAllTowns)
                        )
                )
                .then(Commands.literal("info")
                        .executes(ModCommands::getOwnTownInfo)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(ALL_TOWNS)
                                .executes(ModCommands::getTownInfo)
                        )
                )
        );

        dispatcher.register(Commands.literal("nation")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ModCommands::createNation)
                        )
                )
                .then(Commands.literal("destroy")
                        .executes(ModCommands::destroyOwnNation)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .suggests(ALL_NATIONS)
                                .executes(ModCommands::destroyNation)
                        )
                        .then(Commands.literal("all")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(ModCommands::destroyAllNations)
                        )
                )
        );
    }

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

    public static int createTown(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getOrCreateInstance(player.serverLevel());
        if (townData.getTownByName(name) != null) {
            commandSourceStack.sendFailure(Component.literal("There is already a town named " + name + " in this world"));
            return 0;
        }
        if (townData.getTownByPlayer(player.getUUID()) != null) {
            commandSourceStack.sendFailure(Component.literal("You are already part of another town"));
            return 0;
        }
        townData.createTown(player.getUUID(), name);
        commandSourceStack.sendSuccess(() -> Component.literal("Created town with name " + name), false);
        return 1;
    }


    public static int removeAllOwnClaims(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getInstance(player.serverLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null && town.getOwner().equals(player.getUUID())) {
                commandSourceStack.sendSuccess(() -> Component.literal("Successfully cleared all claims"), false);
                townData.clearAllClaims(town);
                return 1;
            }
        }
        commandSourceStack.sendFailure(TextComponents.NOT_TOWN_OWNER);
        return 0;
    }

    public static int removeAllClaims(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByName(name);
            if (town != null) {
                townData.clearAllClaims(town);
                commandSourceStack.sendSuccess(() -> Component.literal("Successfully cleared all claims of town " + name), false);
                return 1;
            }
        }
        commandSourceStack.sendFailure(Component.literal("There is no town with the name " + name));
        return 0;
    }

    public static int removeAllAllClaims(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            townData.clearAllClaimed();
            commandSourceStack.sendSuccess(() -> Component.literal("Successfully cleared all claims of all towns"), false);
            commandSourceStack.sendSuccess(() -> Component.literal("Destroyed all Towns and Nations"), true);
            return 1;
        } else {
            return 0;
        }
    }

    public static int destroyOwnTown(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getInstance(player.serverLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null && town.getOwner().equals(player.getUUID())) {
                commandSourceStack.sendSuccess(() -> Component.literal("Successfully destroyed own town"), false);
                townData.destroyTown(town);
                return 1;
            }
        }
        commandSourceStack.sendFailure(TextComponents.NOT_TOWN_OWNER);
        return 0;
    }

    public static int destroyTown(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByName(name);
            if (town != null) {
                townData.destroyTown(town);
                commandSourceStack.sendSuccess(() -> Component.literal("Successfully destroyed town " + name), false);
                return 1;
            }
        }
        commandSourceStack.sendFailure(Component.literal("There is no town with the name " + name));
        return 0;
    }

    public static int destroyAllTowns(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            townData.destroyAllTowns();
        }
        commandSourceStack.sendSuccess(() -> Component.literal("Destroyed all Towns and Nations"), true);
        return 1;
    }


    public static int createNation(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getOrCreateInstance(player.serverLevel());
        if (townData.getNationByName(name) != null) {
            commandSourceStack.sendFailure(Component.literal("There is already a nation named " + name + " in this world"));
            return 0;
        }
        Town town = townData.getTownByPlayer(player.getUUID());
        if (town == null) {
            commandSourceStack.sendFailure(Component.literal("You need to be part of a town to create a nation"));
            return 0;
        }

        if (!town.getOwner().equals(player.getUUID())) {
            commandSourceStack.sendFailure(Component.literal("You need to be the town owner to create a nation"));
            return 0;
        }

        int nationThreshold = Services.PLATFORM.getConfig().getNationThreshold();

        if (town.getCitizens().size() < nationThreshold) {
            commandSourceStack.sendFailure(Component.literal("Insufficient citizens to create a nation: " + town.getCitizens().size() + " required: " + nationThreshold));
            return 0;
        }

        townData.createNation(town, name);
        commandSourceStack.sendSuccess(() -> Component.literal("Created nation with name " + name), false);
        return 1;
    }

    public static int destroyOwnNation(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getInstance(player.serverLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null && town.getOwner().equals(player.getUUID())) {
                commandSourceStack.sendSuccess(() -> Component.literal("Successfully destroyed own nation"), false);
                townData.destroyTown(town);
                return 1;
            }
        }
        commandSourceStack.sendFailure(Component.literal("You do not own any nations"));
        return 0;
    }

    public static int destroyNation(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByName(name);
            if (town != null) {
                townData.destroyTown(town);
                return 1;
            }
        }
        commandSourceStack.sendFailure(Component.literal("There is no town with the name " + name));
        return 0;
    }

    public static int destroyAllNations(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            townData.destroyAllNations();
        }
        commandSourceStack.sendSuccess(() -> Component.literal("Destroyed all Nations"), true);
        return 1;
    }

    public static int getTownInfo(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByName(name);
            if (town != null) {
                List<Component> info = buildTownInfo(town);
                for (Component component : info) {
                    commandSourceStack.sendSuccess(() -> component,false);
                }
                return 1;
            }
        }
        commandSourceStack.sendFailure(Component.literal("There is no town with the name " + name));
        return 0;
    }

    public static int getOwnTownInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null) {
                List<Component> info = buildTownInfo(town);
                for (Component component : info) {
                    commandSourceStack.sendSuccess(() -> component,false);
                }
                return 1;
            }
        }
        commandSourceStack.sendFailure(TextComponents.NOT_IN_TOWN);
        return 0;
    }

    protected static List<Component> buildTownInfo(Town town) {
        List<Component> list = new ArrayList<>();
        list.add(Component.literal("Town Info").withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.literal("Name: "+town.getName()));
        list.add(Component.literal("Owner: "+Services.PLATFORM.getLastKnownUserName(town.getOwner())));
        list.add(Component.literal("Money: "+town.getMoney()));
        list.add(Component.literal("Citizens").withStyle(ChatFormatting.UNDERLINE));
        for (UUID uuid : town.getCitizens()) {
            list.add(Component.literal("Citizen: "+Services.PLATFORM.getLastKnownUserName(uuid)));
        }
        list.add(Component.literal("Chunks claimed: "+town.getClaimed().size()));

        return list;
    }
}
