package tfar.nations3.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import tfar.nations3.TextComponents;
import tfar.nations3.platform.Services;
import tfar.nations3.world.*;

import java.util.*;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        TownCommands.registerTownCommands(dispatcher);

        dispatcher.register(Commands.literal("nation")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ModCommands::createNation)
                        )
                )
                .then(Commands.literal("destroy")
                        .executes(ModCommands::destroyOwnNation)
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .suggests(Suggestions.ALL_NATIONS)
                                .executes(ModCommands::destroyNation)
                        )
                        .then(Commands.literal("all")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(ModCommands::destroyAllNations)
                        )
                )
                .then(Commands.literal("info")
                        .executes(ModCommands::getOwnNationInfo)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(Suggestions.ALL_NATIONS)
                                .executes(ModCommands::getNationInfo)
                        )
                )
                .then((Commands.literal("invite")
                        .then(Commands.argument("town",StringArgumentType.string())
                                .suggests(Suggestions.ALL_TOWNS)
                                .executes(ModCommands::createNationInvite))))
                .then(Commands.literal("accept_invite")
                        .then(Commands.argument("nation", StringArgumentType.string())
                                .executes(ModCommands::acceptNationInvite)
                        )
                )
                .then(Commands.literal("alliance_invite")
                        .then(Commands.argument("nation",StringArgumentType.string())
                                .suggests(Suggestions.ALL_NATIONS)
                                .executes(ModCommands::inviteAlliance)
                        )
                )
                .then(Commands.literal("accept_alliance_invite")
                        .then(Commands.argument("nation",StringArgumentType.string())
                                .executes(ModCommands::acceptAllianceInvite)
                        )
                )
        );
    }
    public static int inviteAlliance(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        String nationName = StringArgumentType.getString(ctx, "nation");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Nation ownNation = townData.getNationByPlayer(player.getUUID());

            if (ownNation == null) {
                commandSourceStack.sendFailure(TextComponents.NOT_IN_NATION);
                return 0;
            }

            if (!ownNation.isOwner(player.getUUID())) {
                commandSourceStack.sendFailure(TextComponents.NOT_NATION_OWNER);
                return 0;
            }

            Nation invited = townData.getNationByName(nationName);
            if (invited == null) {
                commandSourceStack.sendFailure(Component.literal("Nation with name "+nationName+" not found"));
                return 0;
            }
            if (invited == ownNation) {
                commandSourceStack.sendFailure(Component.literal("Can't ally with own nation"));
                return 0;
            }

            UUID otherOwner = invited.getOwner();
            MinecraftServer server = commandSourceStack.getServer();
            ServerPlayer otherPlayer = server.getPlayerList().getPlayer(otherOwner);
            if (otherPlayer != null) {
                otherPlayer.sendSystemMessage(Component.literal("Your nation has been invited to ally with nation " + ownNation.getName() + " ")
                        .append(Component.literal("[Accept]").withStyle(Style.EMPTY.applyFormat(ChatFormatting.GREEN)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Join " + ownNation.getName())))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation accept_alliance_invite " + ownNation.getName())))));
                ownNation.addInvite(nationName);

                commandSourceStack.sendSuccess(() -> Component.literal("Invited nation "+nationName+" for an alliance"),false);

                return 1;
            } else {
                commandSourceStack.sendFailure(Component.literal("Nation owner is not online"));
                return 0;
            }

        }
        return 1;
    }

    public static int acceptAllianceInvite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        String nationName = StringArgumentType.getString(ctx, "nation");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Nation ownNation = townData.getNationByPlayer(player.getUUID());

            if (ownNation == null) {
                commandSourceStack.sendFailure(TextComponents.NOT_IN_NATION);
                return 0;
            }

            if (!ownNation.isOwner(player.getUUID())) {
                commandSourceStack.sendFailure(TextComponents.NOT_NATION_OWNER);
                return 0;
            }

            Nation invited = townData.getNationByName(nationName);
            if (invited == null) {
                commandSourceStack.sendFailure(Component.literal("Nation with name "+nationName+" not found"));
                return 0;
            }
            if (invited == ownNation) {
                commandSourceStack.sendFailure(Component.literal("Can't ally with own nation"));
                return 0;
            }
                if (invited.hasAllianceInvite(ownNation.getName())) {
                    invited.addAlliance(ownNation);
                    invited.removeAllianceInvite(ownNation.getName());
                    commandSourceStack.sendSuccess(() -> Component.literal("You are now allied with nation " + invited.getName()), false);

                    ServerPlayer hostPlayer = commandSourceStack.getServer().getPlayerList().getPlayer(invited.getOwner());
                    if (hostPlayer != null) {
                        player.sendSystemMessage(Component.literal(ownNation.getName() + " has accepted your alliance"));
                    }

                    return 1;
                } else {
                    commandSourceStack.sendFailure(Component.literal("Invalid invite"));
                    return 0;
            }
        }
        return 0;
    }


    public static int createNationInvite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        String townName = StringArgumentType.getString(ctx, "town");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Nation ownNation = townData.getNationByPlayer(player.getUUID());

            if (ownNation == null) {
                commandSourceStack.sendFailure(TextComponents.NOT_IN_NATION);
                return 0;
            }

            if (!ownNation.isOwner(player.getUUID())) {
                commandSourceStack.sendFailure(TextComponents.NOT_NATION_OWNER);
                return 0;
            }

            Town invited = townData.getTownByName(townName);
            if (invited == null) {
                commandSourceStack.sendFailure(Component.literal("Town with name "+townName+" not found"));
                return 0;
            }

            if (ownNation.containsTown(invited)) {
                commandSourceStack.sendFailure(Component.literal("Town is already in nation"));
                return 0;
            }

            UUID otherOwner = invited.getOwner();
            MinecraftServer server = commandSourceStack.getServer();
            ServerPlayer otherPlayer = server.getPlayerList().getPlayer(otherOwner);
            if (otherPlayer != null) {
                otherPlayer.sendSystemMessage(Component.literal("Your town has been invited to join nation " + ownNation.getName() + " ")
                        .append(Component.literal("[Accept]").withStyle(Style.EMPTY.applyFormat(ChatFormatting.GREEN)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Join " + ownNation.getName())))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nation accept_invite " + ownNation.getName())))));
                ownNation.addInvite(townName);
                return 1;
            } else {
                commandSourceStack.sendFailure(Component.literal("Town owner is not online"));
                return 0;
            }


        }
        commandSourceStack.sendFailure(TextComponents.INSUFFICIENT_PERMISSION);
        return 0;
    }

    public static int acceptNationInvite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        String nationName = StringArgumentType.getString(ctx, "nation");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town ownTown = townData.getTownByPlayer(player.getUUID());
            if (ownTown == null) {
                commandSourceStack.sendFailure(TextComponents.NOT_IN_TOWN);
                return 0;
            }

            if (!ownTown.isOwner(player.getUUID())) {
                commandSourceStack.sendFailure(TextComponents.NOT_TOWN_OWNER);
                return 0;
            }

            Nation existing = townData.getNationByTown(ownTown);
            if (existing != null) {
                commandSourceStack.sendFailure(Component.literal("Already in nation " + existing.getName()));
                return 0;
            } else {
                Nation invitedNation = townData.getNationByName(nationName);

                if (invitedNation == null) {
                    commandSourceStack.sendFailure(Component.literal("No such nation "+nationName));
                    return 0;
                }

                if (invitedNation.hasInvite(ownTown.getName())) {
                    invitedNation.addTown(ownTown);
                    invitedNation.removeInvite(ownTown.getName());
                    commandSourceStack.sendSuccess(() -> Component.literal("You are now part of nation " + invitedNation.getName()), false);
                    return 1;
                } else {
                    commandSourceStack.sendFailure(Component.literal("Invalid invite"));
                    return 0;
                }
            }
        }
        return 0;
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

    public static int getNationInfo(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Nation nation = townData.getNationByName(name);
            if (nation != null) {
                List<Component> info = buildNationInfo(nation);
                for (Component component : info) {
                    commandSourceStack.sendSuccess(() -> component, false);
                }
                return 1;
            }
        }
        commandSourceStack.sendFailure(Component.literal("There is no nation with the name " + name));
        return 0;
    }

    public static int getOwnNationInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Nation nation = townData.getNationByPlayer(player.getUUID());
            if (nation != null) {
                List<Component> info = buildNationInfo(nation);
                for (Component component : info) {
                    commandSourceStack.sendSuccess(() -> component, false);
                }
                return 1;
            }
        }
        commandSourceStack.sendFailure(TextComponents.NOT_IN_NATION);
        return 0;
    }

    protected static List<Component> buildNationInfo(Nation nation) {
        List<Component> list = new ArrayList<>();
        list.add(Component.literal("Nation Info").withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.literal("Name: " + nation.getName()));
        list.add(Component.literal("Owner: " + Services.PLATFORM.getLastKnownUserName(nation.getOwner())));
        list.add(Component.literal("Money: " + nation.getMoney()));
        list.add(Component.literal("Towns").withStyle(ChatFormatting.UNDERLINE));
        for (Town town : nation.getTowns()) {
            list.add(Component.literal("Town: " + town.getName()));
        }

        return list;
    }
}
