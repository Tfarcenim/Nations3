package tfar.nations3.menu;

import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.nations3.init.ModBlocks;
import tfar.nations3.init.ModMenuTypes;
import tfar.nations3.network.client.S2CTownInfoPacket;
import tfar.nations3.platform.Services;
import tfar.nations3.world.*;

import java.util.List;
import java.util.Objects;

public class ClaimingTableMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;

    public final TownInfos townInfos;

    private final List<TownInfosSlot> townInfoSlots = Lists.newArrayList();

    private final List<TownInfo> remoteTownInfoSlots = Lists.newArrayList();
    private final Player player;
    private final TownData townData;
    private Town town;

    public ClaimingTableMenu(int id, Inventory inventory) {
        this(id,inventory,ContainerLevelAccess.NULL,new ClientTownInfos(81));
    }

    public ClaimingTableMenu(int id, Inventory inventory, ContainerLevelAccess access,TownInfos townInfos) {
        super(ModMenuTypes.CLAIMING_TABLE, id);
        this.access = access;
        this.townInfos = townInfos;
        this.player = inventory.player;

        townData = !player.level().isClientSide ? TownData.getInstance((ServerLevel) player.level()) : null;
        if (townData != null) {
            town = townData.getTownByPlayer(player.getUUID());
        }

        /*addSlot(new Slot(container,0,0,0){
            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.getItem() instanceof MapItem;
            }
        });*/

        int startY = 77;

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + startY));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142 + startY));
        }
        addTownInfos(townInfos);
    }

    protected void addTownInfos(TownInfos pArray) {
        for(int i = 0; i < pArray.getCount(); ++i) {
            this.addTownInfoSlot(TownInfosSlot.forContainer(pArray, i));
        }
    }

    protected TownInfosSlot addTownInfoSlot(TownInfosSlot pIntValue) {
        this.townInfoSlots.add(pIntValue);
        this.remoteTownInfoSlots.add(null);
        return pIntValue;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        for(int slot = 0; slot < this.townInfoSlots.size(); ++slot) {
            TownInfosSlot $$4 = this.townInfoSlots.get(slot);
            TownInfo townInfo = $$4.get();
            if ($$4.checkAndClearUpdateFlag()) {
                this.synchronizeTownInfoSlotToRemote(slot, townInfo);
            }

            this.synchronizeTownInfoSlotToRemote(slot, townInfo);
        }
    }

    private void synchronizeTownInfoSlotToRemote(int slot, TownInfo townInfo) {
        TownInfo $$2 = this.remoteTownInfoSlots.get(slot);
            if (!Objects.equals($$2,townInfo)) {
                this.remoteTownInfoSlots.set(slot, townInfo);
                broadcastTownInfoValue(slot,townInfo);
            }
    }

    private void broadcastTownInfoValue(int slot, TownInfo townInfo) {
        Services.PLATFORM.sendToClient(new S2CTownInfoPacket(containerId, slot, townInfo), (ServerPlayer) player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.CLAIMING_TABLE);
    }

    public ContainerLevelAccess getAccess() {
        return access;
    }

    public void setTownInfo(int index, TownInfo value) {
        this.townInfoSlots.get(index).set(value);
    }
}
