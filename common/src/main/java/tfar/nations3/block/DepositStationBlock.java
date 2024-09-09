package tfar.nations3.block;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tfar.nations3.TextComponents;
import tfar.nations3.platform.Services;
import tfar.nations3.world.Town;
import tfar.nations3.world.TownData;

public class DepositStationBlock extends Block {
    public DepositStationBlock(Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            TownData townData = TownData.getInstance((ServerLevel) pLevel);
            if (townData != null) {
                Town town = townData.getTownByPlayer(player.getUUID());
                if (town != null) {
                    ItemStack stack = player.getItemInHand(pHand);
                    if (stack.isEmpty()) {
                        player.displayClientMessage(Component.literal("Balance: " + town.getMoney()), false);
                    } else {
                        Item item = stack.getItem();
                        Object2LongMap<Item> depositValues = Services.PLATFORM.getConfig().getDepositValues();
                        if (depositValues.containsKey(item)) {
                            long value = stack.getCount() * depositValues.getLong(item);
                            town.personalDeposit(player.getUUID(),value);
                            if (!player.getAbilities().instabuild) {
                            player.setItemInHand(pHand, ItemStack.EMPTY);
                            }
                            player.displayClientMessage(Component.literal("Deposited " + value + " money for " + town.getName() +" under "+player.getGameProfile().getName()), false);
                            player.displayClientMessage(Component.literal("New balance: " + town.getInfo(player.getUUID()).money), false);
                        }
                    }
                } else {
                    player.displayClientMessage(TextComponents.NOT_IN_TOWN, true);
                }
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
}
