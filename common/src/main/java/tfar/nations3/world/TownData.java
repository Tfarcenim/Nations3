package tfar.nations3.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import tfar.nations3.Nations3;

import java.util.*;

public class TownData extends SavedData {

    private List<Town> towns = new ArrayList<>();
    private Map<String,Town> towns_by_name = new HashMap<>();
    private final ServerLevel level;

    public TownData(ServerLevel level) {
        this.level = level;
    }

    public Town createTown(UUID owner,String name) {
        if (towns_by_name.get(name) != null) {
            return null;
        }
        Town town = new Town(this,owner,name);
        towns.add(town);
        towns_by_name.put(town.getName(),town);
        return town;
    }

    public void destroyTown(Town town) {
        if (town != null) {
            towns.remove(town);
            towns_by_name.remove(town.getName());
            setDirty();
        }
    }

    @Nullable
    public Town getOwnerOf(ChunkPos pos) {
        for (Town town : towns) {
            if (town.hasClaim(pos)) {
                return town;
            }
        }
        return null;
    }

    @Nullable
    public Town getTownByPlayer(UUID uuid) {
        for (Town town : towns) {
            if (town.containsPlayer(uuid))return town;
        }
        return null;
    }

    @Nullable
    public Town getTownByName(String name) {
        return towns_by_name.get(name);
    }


    @Nullable
    public static TownData getInstance(ServerLevel serverLevel) {
        return serverLevel.getDataStorage()
                .get(compoundTag -> loadStatic(compoundTag, serverLevel), name(serverLevel));
    }

    public static TownData getOrCreateInstance(ServerLevel serverLevel) {
        return serverLevel.getDataStorage()
                .computeIfAbsent(compoundTag -> loadStatic(compoundTag,serverLevel),
                        () -> new TownData(serverLevel),name(serverLevel));
    }

    private static String name(ServerLevel level) {
        return  Nations3.MOD_ID+"_"+level.dimension().location().toString().replace(':','.');
    }

    public static TownData getOrCreateDefaultInstance(MinecraftServer server) {
        return getOrCreateInstance(server.overworld());
    }

    public static TownData loadStatic(CompoundTag compoundTag,ServerLevel level) {
        TownData id = new TownData(level);
        id.load(compoundTag,level);
        return id;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        for (Town town : towns) {
            listTag.add(town.save());
        }
        compoundTag.put("towns",listTag);
        return compoundTag;
    }

    public void load(CompoundTag tag,ServerLevel level) {
        ListTag townsTag = tag.getList("towns", ListTag.TAG_COMPOUND);
        for (Tag tag1 : townsTag) {
            loadTown((CompoundTag) tag1);
        }
    }

    protected void loadTown(CompoundTag tag) {
        Town town = new Town(this);
        town.load(tag);
        towns.add(town);
        towns_by_name.put(town.getName(),town);
    }
}
