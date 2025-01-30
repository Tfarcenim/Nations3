package tfar.nations3.world;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;
import java.util.UUID;

public interface ChunkOwner {
    Set<ChunkPos> getDirectClaimed();
    default boolean hasClaim(ChunkPos pos) {
        return getDirectClaimed().contains(pos);
    }

    default boolean claim(ChunkPos pos) {
        boolean add = getDirectClaimed().add(pos);
        if (add) setDirty();
        return add;
    }

    default boolean unClaim(ChunkPos pos) {
        boolean remove = getDirectClaimed().remove(pos);
        if (remove) setDirty();
        return remove;
    }

    void setDirty();

    String getName();
    void setName(String newName);
    TownData getData();

    void setMoney(long money);
    long getMoney();
    default void addMoney(long money) {
        setMoney(getMoney() + money);
    }
    boolean containsCitizen(UUID uuid);
    Set<UUID> getAllCitizens();

    default void broadcastMessage(Component component) {
        MinecraftServer server = getData().level.getServer();
        for (UUID uuid : getAllCitizens()) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                player.displayClientMessage(component,false);
            }
        }
    }
}
