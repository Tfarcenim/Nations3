package tfar.nations3.world;

import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Rebellion {

    final Town starter;
    final Set<Town> members = new HashSet<>();
    boolean active;

    final Set<UUID> approve = new HashSet<>();

    public Rebellion(Town starter) {
        this.starter = starter;
        members.add(starter);
        approve.add(starter.getOwner());
    }

    public Town getStarter() {
        return starter;
    }

    public void activate() {
        active = true;
        TownData townData = starter.getTownData();
        Nation nation = townData.getNationByTown(starter);
        if (nation != null) {
            Nation newNation = townData.createNation(starter,starter.getName()+"-state");
            for (Town town : members) {
                nation.removeTown(town);
                newNation.addTown(town);
            }

            Set<ChunkPos> contested = new HashSet<>();
            members.forEach(town -> contested.addAll(town.getClaimed()));

            townData.addWar(nation,newNation,contested);
        }
    }

    public void addVote(UUID uuid) {
        approve.add(uuid);
    }

    public double getApproval() {
        int count = members.stream().mapToInt(town -> town.getCitizens().size()).sum();
        return (double) approve.size()/count;
    }

}
