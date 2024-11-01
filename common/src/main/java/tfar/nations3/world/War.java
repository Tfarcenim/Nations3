package tfar.nations3.world;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public class War {

    public static final int BATTLE_TIME = 20 * 60;

    private final Nation attacker;
    private final Nation defender;

    private int attackerKills;
    private int defenderKills;
    long ticksElapsed;
    final Set<ChunkPos> contested;
    boolean finished;

    public War(Nation attacker, Nation defender,Set<ChunkPos> contested) {
        this.attacker = attacker;
        this.defender = defender;
        this.contested = contested;
    }


    boolean tick() {
        ticksElapsed++;
        return ticksElapsed> BATTLE_TIME;
    }

    Nation getWinner() {
        if (attackerKills > defenderKills) {
            return attacker;
        } else {
            return defender;
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
