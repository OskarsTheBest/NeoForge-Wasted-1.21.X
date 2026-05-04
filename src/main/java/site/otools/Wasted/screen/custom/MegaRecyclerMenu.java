package site.otools.Wasted.screen.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.block.entity.MegaRecyclerBlockEntity;
import site.otools.Wasted.screen.ModMenuTypes;


public class MegaRecyclerMenu extends AbstractContainerMenu {
    public final MegaRecyclerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public MegaRecyclerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public MegaRecyclerMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MEGA_RECYCLER_MENU.get(), pContainerId);

        if (!(entity instanceof MegaRecyclerBlockEntity mega)) {
            throw new IllegalStateException("BlockEntity is not a MegaRecyclerBlockEntity: " + entity);
        }

        this.blockEntity = mega;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        var handler = mega.itemHandler;

        // Inputs: slots 0-5 (2-wide x 3-row grid)
        this.addSlot(new SlotItemHandler(handler, 0, 16, 18));
        this.addSlot(new SlotItemHandler(handler, 1, 34, 18));
        this.addSlot(new SlotItemHandler(handler, 2, 16, 36));
        this.addSlot(new SlotItemHandler(handler, 3, 34, 36));
        this.addSlot(new SlotItemHandler(handler, 4, 16, 54));
        this.addSlot(new SlotItemHandler(handler, 5, 34, 54));

        // Outputs: slots 6-17 (4-wide x 3-row grid)
        this.addSlot(new SlotItemHandler(handler, 6, 89, 18));
        this.addSlot(new SlotItemHandler(handler, 7, 107, 18));
        this.addSlot(new SlotItemHandler(handler, 8, 125, 18));
        this.addSlot(new SlotItemHandler(handler, 9, 143, 18));
        this.addSlot(new SlotItemHandler(handler, 10, 89, 36));
        this.addSlot(new SlotItemHandler(handler, 11, 107, 36));
        this.addSlot(new SlotItemHandler(handler, 12, 125, 36));
        this.addSlot(new SlotItemHandler(handler, 13, 143, 36));
        this.addSlot(new SlotItemHandler(handler, 14, 89, 54));
        this.addSlot(new SlotItemHandler(handler, 15, 107, 54));
        this.addSlot(new SlotItemHandler(handler, 16, 125, 54));
        this.addSlot(new SlotItemHandler(handler, 17, 143, 54));
        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;
        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = MegaRecyclerBlockEntity.SLOT_COUNT;
    private static final int TE_INPUT_SLOT_COUNT = MegaRecyclerBlockEntity.LAST_INPUT_SLOT - MegaRecyclerBlockEntity.FIRST_INPUT_SLOT + 1;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // Player inventory -> input slots only (so we don't dump into outputs)
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INPUT_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // Mega recycler slot -> player inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return ContainerLevelAccess.create(level, blockEntity.getBlockPos())
                .evaluate((level, pos) -> {
                    Block block = level.getBlockState(pos).getBlock();
                    return block == ModBlocks.MEGA_RECYCLER.get() &&
                            pPlayer.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
                }, true);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
    for (int i = 0; i <9; ++i){
        this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }
    }
}