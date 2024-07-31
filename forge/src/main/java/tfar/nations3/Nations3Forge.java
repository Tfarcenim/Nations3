package tfar.nations3;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.nations3.client.ModClientForge;
import tfar.nations3.datagen.ModDatagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod(Nations3.MOD_ID)
public class Nations3Forge {

    public static Map<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();

    public Nations3Forge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,TomlConfig.SERVER_SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onInitialize);
        bus.addListener(this::registerObjs);
        bus.addListener(ModDatagen::gather);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        if (FMLEnvironment.dist.isClient()) {
            ModClientForge.init(bus);
        }
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        Nations3.init();
    }

    public void registerObjs(RegisterEvent event) {
        for (Map.Entry<Registry<?>,List<Pair<ResourceLocation, Supplier<?>>>> entry : registerLater.entrySet()) {
            Registry<?> registry = entry.getKey();
            List<Pair<ResourceLocation, Supplier<?>>> toRegister = entry.getValue();
            for (Pair<ResourceLocation,Supplier<?>> pair : toRegister) {
                event.register((ResourceKey<? extends Registry<Object>>)registry.key(),pair.getLeft(),(Supplier<Object>)pair.getValue());
            }
        }
    }

    public void onInitialize(FMLCommonSetupEvent e) {
        registerLater.clear();
    }

    public void commands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

}