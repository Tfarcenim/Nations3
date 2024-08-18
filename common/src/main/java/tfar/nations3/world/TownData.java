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
import tfar.nations3.platform.Services;

import java.util.*;

public class TownData extends SavedData {

    private List<Town> towns = new ArrayList<>();
    private Map<String,Town> towns_by_name = new HashMap<>();

    private List<Nation> nations = new ArrayList<>();
    private Map<String,Nation> nations_by_name = new HashMap<>();

    private final ServerLevel level;

    public TownData(ServerLevel level) {
        this.level = level;
    }

    public Nation createNation(Town capital,String name) {
        Nation existing = getNationByTown(capital);
        if (existing != null) return null;
        Nation nation = new Nation(this, capital,name);
        nations.add(nation);
        nations_by_name.put(nation.getName(),nation);
        return nation;
    }

    public void destroyNation(Nation nation) {
        nations.remove(nation);
        nations_by_name.remove(nation.getName());
        setDirty();
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

    public void clearAllClaims(Town town) {
        town.clearClaimed();
    }

    public void destroyTown(Town town) {
        if (town != null) {
            towns.remove(town);
            towns_by_name.remove(town.getName());
            for (Nation nation : nations) {
                nation.removeTown(town);
            }
        }
    }

    public static final int INTERVAL = ServerLevel.TICKS_PER_DAY * 7;

    public void tick() {
        if (level.getDayTime() % INTERVAL == 0) {
            payRent();
        }
    }

    public void payRent() {
        for (Town town : towns) {
            Set<ChunkPos> claimed = town.getClaimed();
            int size = claimed.size();
            if (size > 0) {
                long rentPayment = size * Services.PLATFORM.getConfig().getRent();
                if (rentPayment > town.getMoney()) {
                    town.clearClaimed();
                    Nations3.LOG.info("Removed all claimed chunks from town {} as they couldn't afford rent", town.getName());
                } else {
                    town.deposit(-rentPayment);
                }
            }
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
    public Nation getNationByName(String name) {
        return nations_by_name.get(name);
    }

    @Nullable
    public Nation getNationByTown(Town town) {
        for (Nation nation : nations) {
            if (nation.containsTown(town)) {
                return nation;
            }
        }
        return null;
    }

    @Nullable
    public Town getTownByName(String name) {
        return towns_by_name.get(name);
    }

    public void clearAllClaimed() {
        for (Town town : towns) {
            town.clearClaimed();
        }
    }

    public void destroyAllNations() {
        nations.clear();
        nations_by_name.clear();
        setDirty();
    }

    public void destroyAllTowns() {
        destroyAllNations();
        towns.clear();
        nations.clear();
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
        ListTag townTag = new ListTag();
        for (Town town : towns) {
            townTag.add(town.save());
        }
        compoundTag.put("towns",townTag);

        ListTag nationTag = new ListTag();
        for (Nation nation : nations) {
            nationTag.add(nation.save());
        }
        compoundTag.put("nations", nationTag);
        return compoundTag;
    }

    public void load(CompoundTag tag,ServerLevel level) {
        ListTag townsTag = tag.getList("towns", ListTag.TAG_COMPOUND);
        for (Tag tag1 : townsTag) {
            loadTown((CompoundTag) tag1);
        }
        ListTag nationsTag = tag.getList("nations",ListTag.TAG_COMPOUND);
        for (Tag tag1 : nationsTag) {
            loadNation((CompoundTag) tag1);
        }
    }

    protected void loadNation(CompoundTag tag) {
        Nation nation = new Nation(this);
        nation.load(tag);
        nations.add(nation);
    }

    protected void loadTown(CompoundTag tag) {
        Town town = new Town(this);
        town.load(tag);
        towns.add(town);
        towns_by_name.put(town.getName(),town);
    }
}
