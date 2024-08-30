package tfar.nations3.platform;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.world.item.Item;

public interface MLConfig {
    Object2LongMap<Item> getDepositValues();
    long getRent();
    int getNationThreshold();
    double getNationTaxRate();
}
