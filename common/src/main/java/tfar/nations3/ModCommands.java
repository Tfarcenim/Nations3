package tfar.nations3;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import tfar.nations3.world.Town;
import tfar.nations3.world.TownData;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("town")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ModCommands::createTown)
                        )
                )
                .then(Commands.literal("destroy")
                        .executes(ModCommands::destroyOwnTown)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(ModCommands::destroyTown)
                        )
                )
        );
    }

    public static int createTown(CommandContext<CommandSourceStack>ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx,"name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getOrCreateInstance(player.serverLevel());
        if (townData.getTownByName(name) != null) {
            commandSourceStack.sendFailure(Component.literal("There is already a town named "+name +" in this world"));
            return 0;
        }
        if (townData.getTownByPlayer(player.getUUID()) != null) {
            commandSourceStack.sendFailure(Component.literal("You are already part of another town"));
            return 0;
        }
        townData.createTown(player.getUUID(),name);
        commandSourceStack.sendSuccess(() -> Component.literal("Created town with name "+name),false);
        return 1;
    }

    public static int destroyOwnTown(CommandContext<CommandSourceStack>ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getOrCreateInstance(player.serverLevel());
        Town town = townData.getTownByPlayer(player.getUUID());
        if (town!= null) {
            commandSourceStack.sendSuccess(() -> Component.literal("Successfully destroyed own town"),false);
            townData.destroyTown(town);
            return 1;
        } else {
            commandSourceStack.sendFailure(Component.literal("You do not own any towns"));
            return 0;
        }
    }

    public static int destroyTown(CommandContext<CommandSourceStack>ctx) {
        String name = StringArgumentType.getString(ctx,"name");
        CommandSourceStack commandSourceStack = ctx.getSource();
        TownData townData = TownData.getOrCreateInstance(commandSourceStack.getLevel());
        Town town = townData.getTownByName(name);
        if (town!= null) {
            townData.destroyTown(town);
            return 1;
        } else {
            commandSourceStack.sendFailure(Component.literal("There is no town with the name "+name));
            return 0;
        }
    }

}
