package tfar.nations3.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import tfar.nations3.Nations3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TownData extends SavedData {

    private List<Town> towns = new ArrayList<>();
    private Map<String,Town> towns_by_name = new HashMap<>();
    private final ServerLevel level;

    public TownData(ServerLevel level) {
        this.level = level;
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
        return compoundTag;
    }

    public void load(CompoundTag tag,ServerLevel level) {

    }

}
