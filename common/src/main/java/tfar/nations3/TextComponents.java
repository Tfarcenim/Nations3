package tfar.nations3;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextComponents {

    public static final MutableComponent NOT_IN_TOWN = Component.literal("You are not in any towns");
    public static final MutableComponent NOT_IN_NATION = Component.literal("You are not in any nations");
    public static final MutableComponent NOT_TOWN_OWNER = Component.literal("You are not the town owner");
    public static final MutableComponent NOT_NATION_OWNER = Component.literal("You are not the nation owner");
    public static final MutableComponent INSUFFICIENT_PERMISSION = Component.literal("You do not have permission to perform this action");


}
