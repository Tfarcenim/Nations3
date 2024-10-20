package tfar.nations3;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.nations3.platform.MLConfig;

import java.util.List;

public class TomlConfig implements MLConfig {

    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<Server, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }
    public static final List<String> defaults = Lists.newArrayList(
            "minecraft:emerald|100");

    @Override
    public Object2LongMap<Item> getDepositValues() {
        return Server.cache;
    }

    @Override
    public long getRent() {
        return Server.rent.get();
    }

    @Override
    public int getNationThreshold() {
        return Server.nation_threshold.get();
    }

    @Override
    public double getNationTaxRate() {
        return Server.nation_tax_rate.get();
    }

    public static class Server {
        public static ForgeConfigSpec.LongValue rent;
        public static ForgeConfigSpec.IntValue nation_threshold;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> deposit_values;
        public static ForgeConfigSpec.DoubleValue nation_tax_rate;
        public static Object2LongMap<Item> cache = new Object2LongOpenHashMap<>();


        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            rent = builder.defineInRange("rent",200,0,1000000000L);
            nation_threshold = builder.defineInRange("nation_threshold",5,1,1000);
            deposit_values = builder
                    .comment("Deposit values")
                    .defineList("deposit_values",() -> defaults, String.class::isInstance);

            nation_tax_rate = builder.defineInRange("nation_tax_rate",.025,0,1000);

            builder.pop();
        }
    }

    static void configLoad(ModConfigEvent e) {
        if (e.getConfig().getModId().equals(Nations3.MOD_ID)) {
            Server.cache.clear();
            for (String s : Server.deposit_values.get()) {
                String[] split = s.split("\\|");
                try {
                    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(split[0]));
                    Server.cache.put(item, Long.parseLong(split[1]));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
