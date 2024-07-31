package tfar.nations3.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tfar.nations3.menu.ClaimingTableMenu;
import tfar.nations3.world.ClaimDisplay;
import tfar.nations3.world.TownData;

public class ClaimingTableBlock extends Block {
    public ClaimingTableBlock(Properties $$0) {
        super($$0);
    }
    public static final MutableComponent CONTAINER_TITLE = Component.translatable("container.nations3.claiming_table");

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
            //pPlayer.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return InteractionResult.CONSUME;
        }
    }

    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        TownData townData = TownData.getOrCreateInstance((ServerLevel) pLevel);
        return new SimpleMenuProvider((id, inventory, player) -> new ClaimingTableMenu(id, inventory, ContainerLevelAccess.create(pLevel, pPos),
                new ClaimDisplay(townData,player,new ChunkPos(pPos))), CONTAINER_TITLE);
    }
}
