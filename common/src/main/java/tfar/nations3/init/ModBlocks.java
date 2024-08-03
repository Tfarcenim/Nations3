package tfar.nations3.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.nations3.block.ClaimingTableBlock;
import tfar.nations3.block.DepositStationBlock;

public class ModBlocks {
    public static final Block CLAIMING_TABLE = new ClaimingTableBlock(BlockBehaviour.Properties.of());
    public static final Block DEPOSIT_STATION = new DepositStationBlock(BlockBehaviour.Properties.of());
}
