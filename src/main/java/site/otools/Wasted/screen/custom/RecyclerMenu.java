package site.otools.Wasted.screen.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.block.custom.RecyclerBlock;
import site.otools.Wasted.block.entity.PlasticRecyclerBlockEntity;
import site.otools.Wasted.block.entity.RecyclerBlockEntity;
import site.otools.Wasted.screen.ModMenuTypes;

public class RecyclerMenu extends AbstractContainerMenu {
    public final BlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public RecyclerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public RecyclerMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.RECYCLER_MENU.get(), pContainerId);


        if (!(entity instanceof RecyclerBlockEntity) && !(entity instanceof PlasticRecyclerBlockEntity)) {
            throw new IllegalStateException("BlockEntity is not a valid recycler: " + entity);
        }

        this.blockEntity = entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);


        var handler = (entity instanceof RecyclerBlockEntity recycler)
                ? recycler.itemHandler
                : ((PlasticRecyclerBlockEntity) entity).itemHandler;

        this.addSlot(new SlotItemHandler(handler, 0, 34, 34));
        this.addSlot(new SlotItemHandler(handler, 1, 79, 18));
        this.addSlot(new SlotItemHandler(handler, 2, 97, 18));
        this.addSlot(new SlotItemHandler(handler, 3, 115, 18));
        this.addSlot(new SlotItemHandler(handler, 4, 133, 18));
        this.addSlot(new SlotItemHandler(handler, 5, 79, 36));
        this.addSlot(new SlotItemHandler(handler, 6, 97, 36));
        this.addSlot(new SlotItemHandler(handler, 7, 115, 36));
        this.addSlot(new SlotItemHandler(handler, 8, 133, 36));
        this.addSlot(new SlotItemHandler(handler, 9, 79, 54));
        this.addSlot(new SlotItemHandler(handler, 10, 97, 54));
        this.addSlot(new SlotItemHandler(handler, 11, 115, 54));
        this.addSlot(new SlotItemHandler(handler, 12, 133, 54));

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

    private static final int TE_INVENTORY_SLOT_COUNT = 13;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {

            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
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
                    return (block == ModBlocks.RECYCLER.get() ||
                            block == ModBlocks.GLASS_RECYCLER.get() ||
                            block == ModBlocks.METAL_RECYCLER.get() ||
                            block == ModBlocks.PLASTIC_RECYCLER.get()) &&
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
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}