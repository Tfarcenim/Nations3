package tfar.nations3.world;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import tfar.nations3.Nations3;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class War {

    public static final int BATTLE_TIME = 20 * 60;

    private final Nation attacker;
    private final Nation defender;

    private int attackerKills;
    private int defenderKills;
    int ticksElapsed;
    final Set<ChunkPos> contested;
    boolean finished;

    public CustomBossEvent bossEvent;

    public War(Nation attacker, Nation defender,Set<ChunkPos> contested) {
        this.attacker = attacker;
        this.defender = defender;
        this.contested = contested;
        setupBar();
    }

    void setupBar() {
        bossEvent = new CustomBossEvent(Nations3.id(attacker.getName().toLowerCase(Locale.ROOT) +"-"+defender.getName().toLowerCase(Locale.ROOT)),
                Component.literal(attacker.getName().toLowerCase(Locale.ROOT) +"-"+defender.getName().toLowerCase(Locale.ROOT)+" War"));

        trackNation(attacker);
        trackNation(defender);
        bossEvent.setMax(BATTLE_TIME);
    }

    void trackNation(Nation nation) {
        MinecraftServer server = nation.getData().level.getServer();
        for (UUID uuid : nation.getAllCitizens()) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                bossEvent.addPlayer(player);
            } else {
                bossEvent.addOfflinePlayer(uuid);
            }
        }
    }

    void tick() {
        ticksElapsed++;
        bossEvent.setValue(ticksElapsed);
        boolean finished = ticksElapsed> BATTLE_TIME;
        if (finished) {
            onFinish();
        }
    }

    void onFinish() {
        finished = true;
        Nation loser = getLoser();
        Nation winner = getWinner();
        if (winner == attacker) {
            winner.getClaimed().addAll(contested);
            loser.deepUnclaim(contested);
            loser.broadcastMessage(Component.literal("You have lost the battle and contested territory"));
            winner.broadcastMessage(Component.literal("You have won the battle and contested territory"));
        } else {
            loser.broadcastMessage(Component.literal("You have lost the battle and gained nothing"));
            winner.broadcastMessage(Component.literal("You have won the battle and kept territory"));
        }
        bossEvent.removeAllPlayers();

    }

    Nation getWinner() {
        if (attackerKills > defenderKills) {
            return attacker;
        } else {
            return defender;
        }
    }

    Nation getLoser() {
        if (attackerKills > defenderKills) {
            return defender;
        } else {
            return attacker;
        }
    }

    public void trackKill(ServerPlayer attacker, ServerPlayer killed) {
        if (this.attacker.containsCitizen(attacker.getUUID())) {
            if (defender.containsCitizen(killed.getUUID())) {
                attackerKills++;
            }
        } else if (defender.containsCitizen(attacker.getUUID())) {
            if (this.attacker.containsCitizen(killed.getUUID())) {
                defenderKills++;
            }
        }
    }
}
