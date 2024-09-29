package tfar.nations3.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import tfar.nations3.TextComponents;
import tfar.nations3.platform.Services;
import tfar.nations3.world.*;

import java.util.*;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("town")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ModCommands::createTown)
                        )
                )
                .then(Commands.literal("tax_rate")
                        .then(Commands.argument("tax_rate", LongArgumentType.longArg(0)).executes(ModCommands::setTaxRate))
                )
                .then(Commands.literal("accept_invite")
                        .then(Commands.argument("town", StringArgumentType.string())
                                .executes(ModCommands::acceptTownInvite)
                        )
                )
                .then(Commands.literal("invite")
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(ModCommands::createTownInvite)
                        )
                )
                .then(Commands.literal("kick")
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(ModCommands::kickCitizens)
                        )
                )
                .then(Commands.literal("clear_claims")
                        .executes(ModCommands::removeAllOwnClaims)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .suggests(Suggestions.ALL_TOWNS)
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
                                .suggests(Suggestions.ALL_TOWNS)
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
                                .suggests(Suggestions.ALL_TOWNS)
                                .executes(ModCommands::getTownInfo)
                        )
                )

                .then(Commands.literal("permission")
                        .then(Commands.literal("grant")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .then(Commands.argument("permission", StringArgumentType.string())
                                                .executes(ModCommands::grantTownPermission)
                                        )
                                )
                                .executes(ModCommands::removeAllOwnClaims))
                        .then(Commands.literal("revoke")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .then(Commands.argument("permission", StringArgumentType.string())
                                                .executes(ModCommands::revokeTownPermission)
                                        )
                                )
                        )
                        .then(Commands.literal("check")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .executes(ModCommands::checkTownPermissions)
                                )
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
        );
    }

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

    public static int setTaxRate(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        long taxRate = LongArgumentType.getLong(ctx, "tax_rate");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null) {
                if (town.checkPermission(player.getUUID(), TownPermissions.MANAGE_PERSONAL_TAX)) {
                    town.setTaxRate(taxRate);
                    commandSourceStack.sendSuccess(() -> Component.literal("Set town tax rate to " + taxRate), false);
                    return 1;
                } else {
                    commandSourceStack.sendFailure(TextComponents.INSUFFICIENT_PERMISSION);
                    return 0;
                }
            }
        }
        commandSourceStack.sendFailure(TextComponents.NOT_IN_TOWN);
        return 0;
    }

    public static int removeAllOwnClaims(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getInstance(player.serverLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null && town.isOwner(player.getUUID())) {
                commandSourceStack.sendSuccess(() -> Component.literal("Successfully cleared all claims"), false);
                townData.clearAllClaims(town);
                return 1;
            }
        }
        commandSourceStack.sendFailure(TextComponents.NOT_TOWN_OWNER);
        return 0;
    }

    public static int createTownInvite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "players");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null && town.checkPermission(player.getUUID(), TownPermissions.MANAGE_CITIZENS)) {
                for (ServerPlayer otherPlayer : players) {
                    otherPlayer.sendSystemMessage(Component.literal("You have been invited to join town " + town.getName() + " ")
                            .append(Component.literal("[Accept]").withStyle(Style.EMPTY.applyFormat(ChatFormatting.GREEN)
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Join " + town.getName())))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/town accept_invite " + town.getName())))));
                    town.addInvite(otherPlayer.getUUID());
                }
                return 1;
            }
        }
        commandSourceStack.sendFailure(TextComponents.INSUFFICIENT_PERMISSION);
        return 0;
    }

    public static int acceptTownInvite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        String townName = StringArgumentType.getString(ctx, "town");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null) {
                commandSourceStack.sendFailure(Component.literal("Already in town " + town.getName()));
                return 0;
            } else {
                Town invitedTown = townData.getTownByName(townName);
                if (invitedTown != null) {
                    if (invitedTown.hasInvite(player.getUUID())) {
                        invitedTown.addCitizen(player.getUUID());
                        invitedTown.removeInvite(player.getUUID());
                        commandSourceStack.sendSuccess(() -> Component.literal("You are now part of town " + invitedTown.getName()), false);
                        return 1;
                    } else {
                        commandSourceStack.sendFailure(Component.literal("Invalid invite"));
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    public static int kickCitizens(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "players");
        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(player.getUUID());
            if (town != null && town.checkPermission(player.getUUID(), TownPermissions.MANAGE_CITIZENS)) {
                for (ServerPlayer otherPlayer : players) {
                    if (player != otherPlayer) {
                        if (town.containsCitizen(otherPlayer.getUUID())) {
                            otherPlayer.sendSystemMessage(Component.literal("You have been kicked from " + town.getName()));
                            town.removeCitizen(otherPlayer.getUUID());
                        }
                    }
                }
                return 1;
            }
        }
        commandSourceStack.sendFailure(TextComponents.INSUFFICIENT_PERMISSION);
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

    //town permission grant <player> permission_string
    public static int revokeTownPermission(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        String perm = StringArgumentType.getString(ctx, "permission");

        TownPermission townPermission = TownPermissions.getPermission(perm);
        if (townPermission == null) {
            commandSourceStack.sendFailure(Component.literal("No such permission: " + perm));
            return 0;
        }


        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            GameProfile target = GameProfileArgument.getGameProfiles(ctx, "player").iterator().next();
            Town targetTown = townData.getTownByPlayer(target.getId());
            if (targetTown == null) {
                commandSourceStack.sendFailure(Component.literal("Target player is not in town"));
                return 0;
            }

            if (targetTown.isOwner(target.getId())) {
                commandSourceStack.sendFailure(Component.literal("Cannot modify permissions of town owner"));
                return 0;
            }

            if (commandSourceStack.isPlayer()) {
                ServerPlayer owner = commandSourceStack.getPlayerOrException();

                boolean op = commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS);

                Town town = townData.getTownByPlayer(owner.getUUID());

                if (!op && town == null) {
                    commandSourceStack.sendFailure(TextComponents.NOT_IN_TOWN);
                    return 0;
                }

                if (!op && targetTown != town) {
                    commandSourceStack.sendFailure(Component.literal("Cannot modify other town's permission"));
                    return 0;
                }

                if (!op && !town.isOwner(owner.getUUID())) {
                    commandSourceStack.sendFailure(TextComponents.NOT_TOWN_OWNER);
                }

            }

            targetTown.revokePermission(target.getId(), townPermission);
            return 1;

        }
        return 0;
    }


    //town permission grant <player> permission_string
    public static int grantTownPermission(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        String perm = StringArgumentType.getString(ctx, "permission");

        TownPermission townPermission = TownPermissions.getPermission(perm);
        if (townPermission == null) {
            commandSourceStack.sendFailure(Component.literal("No such permission: " + perm));
            return 0;
        }

        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            GameProfile target = GameProfileArgument.getGameProfiles(ctx, "player").iterator().next();
            Town targetTown = townData.getTownByPlayer(target.getId());
            if (targetTown == null) {
                commandSourceStack.sendFailure(Component.literal("Target player is not in town"));
                return 0;
            }

            if (targetTown.isOwner(target.getId())) {
                commandSourceStack.sendFailure(Component.literal("Cannot modify permissions of town owner"));
                return 0;
            }

            if (commandSourceStack.isPlayer()) {
                ServerPlayer owner = commandSourceStack.getPlayerOrException();

                boolean op = commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS);

                Town town = townData.getTownByPlayer(owner.getUUID());

                if (!op && town == null) {
                    commandSourceStack.sendFailure(TextComponents.NOT_IN_TOWN);
                    return 0;
                }


                if (!op && targetTown != town) {
                    commandSourceStack.sendFailure(Component.literal("Cannot modify other town's permission"));
                    return 0;
                }

                if (!op && !town.isOwner(owner.getUUID())) {
                    commandSourceStack.sendFailure(TextComponents.NOT_TOWN_OWNER);
                }

            }

            targetTown.grantPermission(target.getId(), townPermission);
            return 1;

        }
        return 0;
    }

    public static int checkTownPermissions(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        Collection<GameProfile> gameProfiles = GameProfileArgument.getGameProfiles(ctx, "player");

        GameProfile gameProfile = gameProfiles.iterator().next();

        TownData townData = TownData.getInstance(commandSourceStack.getLevel());
        if (townData != null) {
            Town town = townData.getTownByPlayer(gameProfile.getId());
            if (town == null) {
                commandSourceStack.sendFailure(Component.literal("Target is not in a town"));
                return 0;
            }

            commandSourceStack.sendSuccess(() -> Component.literal("Permissions for " + Services.PLATFORM.getLastKnownUserName(gameProfile.getId()))
                    .withStyle(ChatFormatting.UNDERLINE), false);
            Set<TownPermission> permissions = town.getPermissions(gameProfile.getId());
            List<Component> components = new ArrayList<>();
            if (permissions.isEmpty()) {
                components.add(Component.literal("None"));
            } else {
                for (TownPermission townPermission : permissions) {
                    components.add(Component.literal(townPermission.key()));
                }
            }

            for (Component component : components) {
                commandSourceStack.sendSuccess(() -> component, false);
            }

        }
        return 1;
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
                    commandSourceStack.sendSuccess(() -> component, false);
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
                    commandSourceStack.sendSuccess(() -> component, false);
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
        list.add(Component.literal("Name: " + town.getName()));
        list.add(Component.literal("Owner: " + Services.PLATFORM.getLastKnownUserName(town.getOwner())));
        list.add(Component.literal("Money: " + town.getMoney()));
        list.add(Component.literal("Tax Rate: " + town.getTaxRate()));
        list.add(Component.literal("Citizens").withStyle(ChatFormatting.UNDERLINE));
        for (UUID uuid : town.getCitizens()) {
            list.add(Component.literal("Citizen: " + Services.PLATFORM.getLastKnownUserName(uuid)));
        }
        list.add(Component.literal("Chunks claimed: " + town.getClaimed().size()));

        return list;
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
        //ist.add(Component.literal("Chunks claimed: " + nation.getClaimed().size()));

        return list;
    }
}
