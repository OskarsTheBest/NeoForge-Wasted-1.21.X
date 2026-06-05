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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.block.entity.CasinoBlockEntity;
import site.otools.Wasted.item.ModItems;
import site.otools.Wasted.screen.ModMenuTypes;

public class CasinoMenu extends AbstractContainerMenu {

    public static final int BTN_BET_MINUS = 0;
    public static final int BTN_BET_PLUS = 1;
    public static final int BTN_DEAL = 2;
    public static final int BTN_HIT = 3;
    public static final int BTN_STAND = 4;
    public static final int BTN_BET_MINUS_10 = 5;
    public static final int BTN_BET_PLUS_10 = 6;

    private static final int PLAYER_SLOT_COUNT = 36; // inv (27) + hotbar (9)

    public final CasinoBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public CasinoMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(CasinoBlockEntity.DATA_SIZE));
    }

    public CasinoMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CASINO_MENU.get(), id);
        if (!(entity instanceof CasinoBlockEntity casino)) {
            throw new IllegalStateException("BlockEntity is not a CasinoBlockEntity: " + entity);
        }
        this.blockEntity = casino;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // key slot (left), coin slot (below it)
        this.addSlot(new SlotItemHandler(casino.itemHandler, CasinoBlockEntity.KEY_SLOT, 13, 30));
        this.addSlot(new SlotItemHandler(casino.itemHandler, CasinoBlockEntity.COIN_SLOT, 13, 58));

        addDataSlots(data);
    }



    public int phase() { return data.get(CasinoBlockEntity.IDX_PHASE); }
    public int gameId() { return data.get(CasinoBlockEntity.IDX_GAME); }
    public int bet() { return data.get(CasinoBlockEntity.IDX_BET); }
    public int result() { return data.get(CasinoBlockEntity.IDX_RESULT); }
    public int coins() { return data.get(CasinoBlockEntity.IDX_COINS); }
    public int playerCount() { return data.get(CasinoBlockEntity.IDX_PLAYER_COUNT); }
    public int dealerCount() { return data.get(CasinoBlockEntity.IDX_DEALER_COUNT); }
    public int playerTotal() { return data.get(CasinoBlockEntity.IDX_PLAYER_TOTAL); }
    public int dealerTotal() { return data.get(CasinoBlockEntity.IDX_DEALER_TOTAL); }
    public boolean holeHidden() { return data.get(CasinoBlockEntity.IDX_HOLE_HIDDEN) == 1; }
    public int playerCard(int i) { return data.get(CasinoBlockEntity.IDX_PLAYER_CARDS + i); }
    public int dealerCard(int i) { return data.get(CasinoBlockEntity.IDX_DEALER_CARDS + i); }



    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (level.isClientSide()) return true;
        switch (id) {
            case BTN_BET_MINUS -> blockEntity.setBet(-1);
            case BTN_BET_PLUS -> blockEntity.setBet(1);
            case BTN_BET_MINUS_10 -> blockEntity.setBet(-10);
            case BTN_BET_PLUS_10 -> blockEntity.setBet(10);
            case BTN_DEAL -> blockEntity.deal(player);
            case BTN_HIT -> blockEntity.hit(player);
            case BTN_STAND -> blockEntity.stand(player);
            default -> { return false; }
        }
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!level.isClientSide()) {
            blockEntity.refundIfInProgress(player);
        }
    }



    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        int keySlot = PLAYER_SLOT_COUNT;
        int coinSlot = PLAYER_SLOT_COUNT + 1;

        if (index < PLAYER_SLOT_COUNT) {
            if (sourceStack.is(ModItems.BLACKJACK_KEY.get())) {
                if (!moveItemStackTo(sourceStack, keySlot, keySlot + 1, false)) return ItemStack.EMPTY;
            } else if (sourceStack.is(ModItems.COIN.get())) {
                if (!moveItemStackTo(sourceStack, coinSlot, coinSlot + 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(sourceStack, 0, PLAYER_SLOT_COUNT, false)) return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();
        sourceSlot.onTake(player, sourceStack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return ContainerLevelAccess.create(level, blockEntity.getBlockPos())
                .evaluate((lvl, pos) -> lvl.getBlockState(pos).getBlock() == ModBlocks.CASINO.get()
                        && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
    }

    private void addPlayerInventory(Inventory inv) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inv, l + i * 9 + 9, 48 + l * 18, 140 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 48 + i * 18, 198));
        }
    }
}
