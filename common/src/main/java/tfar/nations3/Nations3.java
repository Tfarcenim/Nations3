package tfar.nations3;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfar.nations3.init.ModBlocks;
import tfar.nations3.init.ModCreativeTabs;
import tfar.nations3.init.ModItems;
import tfar.nations3.init.ModMenuTypes;
import tfar.nations3.network.PacketHandler;
import tfar.nations3.platform.Services;
import tfar.nations3.world.TownData;

import java.util.stream.Stream;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class Nations3 {

    public static final String MOD_ID = "nations3";
    public static final String MOD_NAME = "Nations3";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Services.PLATFORM.registerAll(ModBlocks.class, BuiltInRegistries.BLOCK, Block.class);
        Services.PLATFORM.registerAll(ModItems.class, BuiltInRegistries.ITEM, Item.class);
        Services.PLATFORM.registerAll(ModMenuTypes.class, BuiltInRegistries.MENU, (Class<MenuType<?>>) (Object)MenuType.class);
        Services.PLATFORM.registerAll(ModCreativeTabs.class, BuiltInRegistries.CREATIVE_MODE_TAB, CreativeModeTab.class);
        PacketHandler.registerPackets();
    }

    public static void tickLevel(ServerLevel level) {
        TownData townData = TownData.getInstance(level);
        if (townData != null) {
            townData.tick();
        }
    }

    public static void afterSleep(ServerLevel level, long newTime) {
        long oldTime = level.getDayTime();
        long weekOld = oldTime / TownData.INTERVAL;
        long weekNew = newTime / TownData.INTERVAL;
        long diff = weekNew - weekOld;
        TownData townData = TownData.getInstance(level);
        if (townData != null) {
            for (int i = 0; i < diff;i++) {
                townData.payRent();
            }
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID,path);
    }

    public static Stream<Block> getKnownBlocks() {
        return getKnown(BuiltInRegistries.BLOCK);
    }
    public static Stream<Item> getKnownItems() {
        return getKnown(BuiltInRegistries.ITEM);
    }

    public static <V> Stream<V> getKnown(Registry<V> registry) {
        return registry.stream().filter(o -> registry.getKey(o).getNamespace().equals(MOD_ID));
    }
}