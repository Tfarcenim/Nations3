package tfar.nations3;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import tfar.nations3.platform.MLConfig;

public class TomlConfig implements MLConfig {

    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<Server, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }
    

    public static class Server {
        public static ForgeConfigSpec.IntValue weekly_cost;
        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            weekly_cost = builder.defineInRange("weekly_cost",1,0,1000000000);
            builder.pop();
        }
    }
}
