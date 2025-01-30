package tfar.nations3.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import tfar.nations3.world.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestCommands {

    public static final UUID UUID0 = UUID.fromString("e9094d8f-7747-41c7-9158-ed692dc11f74");
    public static final UUID UUID1 = UUID.fromString("68e8ef87-d059-408b-aede-ab7102dc20c5");
    public static final UUID UUID2 = UUID.fromString("6d66dbaf-8eac-4f8f-b81f-6760d8839e1a");
    public static final UUID UUID3 = UUID.fromString("d0f99347-be8d-499d-83c8-3666af6e2186");
    public static final UUID UUID4 = UUID.fromString("9ae11024-002a-49bf-80be-a3eedabe6922");
    public static final UUID UUID5 = UUID.fromString("1ab3b534-3012-4fa0-ad67-595563c43ce4");
    public static final UUID UUID6 = UUID.fromString("03079bb8-59e9-4134-b133-daad44092293");
    public static final UUID UUID7 = UUID.fromString("341b85c5-329f-4bcb-ba42-76c20599b877");
    public static final UUID UUID8 = UUID.fromString("cdd9b0e1-b69e-48ee-9a8a-3455a191fe72");
    public static final UUID UUID9 = UUID.fromString("ee8346e6-9a5f-409b-b854-f90d9822749b");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("modtest")
                .then(Commands.literal("create_nation").executes(TestCommands::createNation))
                .then(Commands.literal("create_rival_nation").executes(TestCommands::createRivalNation))
                .then(Commands.literal("create_dummy_war").executes(TestCommands::createDummyWar))
                .then(Commands.literal("create_rival_claims").executes(TestCommands::addRivalClaimedChunks))

        );
    }

    static int createNation(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        ServerPlayer player = commandSourceStack.getPlayerOrException();
        TownData townData = TownData.getOrCreateInstance(player.server.overworld());
        UUID uuid = player.getUUID();
        Town existingTown = townData.getTownByPlayer(uuid);
        if (existingTown == null) {
            existingTown = townData.createTown(uuid, "town0");
            commandSourceStack.sendSystemMessage(Component.literal("Created town"));
        };
        Nation existingNation = townData.getNationByPlayer(uuid);
        if (existingNation == null) {
            townData.createNation(existingTown,"nation0");
            commandSourceStack.sendSystemMessage(Component.literal("Created nation"));
        }
        return 1;
    }

    static int createRivalNation(CommandContext<CommandSourceStack> ctx) {
        int i = 0;
        CommandSourceStack commandSourceStack = ctx.getSource();
        MinecraftServer server = commandSourceStack.getServer();
        TownData townData = TownData.getOrCreateInstance(server.overworld());
        UUID uuid = UUID0;
        Town existingTown = townData.getTownByPlayer(uuid);
        if (existingTown == null) {
            existingTown = townData.createTown(uuid, "rival_town"+i);
            commandSourceStack.sendSystemMessage(Component.literal("Created rival town"));
        };
        Nation existingNation = townData.getNationByPlayer(uuid);
        if (existingNation == null) {
            townData.createNation(existingTown,"rival_nation"+i);
            commandSourceStack.sendSystemMessage(Component.literal("Created rival nation"));
        }
        return 1;
    }

    static int createDummyWar(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = ctx.getSource();
        MinecraftServer server = commandSourceStack.getServer();
        TownData townData = TownData.getOrCreateInstance(server.overworld());
        Nation nation0 = townData.getNationByName("nation0");

        if (nation0 == null) {
            createNation(ctx);
            nation0 = townData.getNationByName("nation0");
        }

        Nation rival_nation0 = townData.getNationByName("rival_nation0");
        if (rival_nation0 == null) {
            createRivalNation(ctx);
            rival_nation0 = townData.getNationByName("rival_nation0");
        }

        Set<CompletedWar> completedWars = townData.getCompletedWars();
        completedWars.add(new CompletedWar(nation0,rival_nation0));
        commandSourceStack.sendSystemMessage(Component.literal("Created fake completed war"));

        return 1;
    }

    static int addRivalClaimedChunks(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int i = 0;
        CommandSourceStack commandSourceStack = ctx.getSource();
        MinecraftServer server = commandSourceStack.getServer();
        ServerLevel overworld = server.overworld();
        TownData townData = TownData.getOrCreateInstance(overworld);
        Nation rival_nation0 = townData.getNationByName("rival_nation0");

        if (rival_nation0 == null) {
            createRivalNation(ctx);
            rival_nation0 = townData.getNationByName("rival_nation");
        }

        for (int z =  -1;z < 2;z++) {
            for (int x = -1;x < 2;x++) {
                ChunkPos chunkPos = new ChunkPos(x,z);
                ChunkOwner chunkOwner = townData.getOwnerOf(chunkPos);
                if (chunkOwner == null) {
                    rival_nation0.claim(chunkPos);
                }
            }
        }

        return 1;
    }

}
