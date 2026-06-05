package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.casino.Blackjack;
import site.otools.Wasted.item.ModItems;
import site.otools.Wasted.screen.custom.CasinoMenu;

/**
 * Casino block entity. Slot 0 = game key (cartridge), slot 1 = coin wallet (wastedmod:coin).
 * Hosts a server-side Blackjack state machine; the masked game state is pushed to the client via
 * {@link ContainerData}.
 */
public class CasinoBlockEntity extends BlockEntity implements MenuProvider {


    public static final int KEY_SLOT = 0;
    public static final int COIN_SLOT = 1;


    public static final int GAME_NONE = 0;
    public static final int GAME_BLACKJACK = 1;


    public static final int PHASE_BET = 0;
    public static final int PHASE_PLAYER = 1;
    public static final int PHASE_RESOLVED = 2;


    public static final int RESULT_NONE = 0;
    public static final int RESULT_WIN = 1;
    public static final int RESULT_LOSE = 2;
    public static final int RESULT_PUSH = 3;
    public static final int RESULT_BLACKJACK = 4;
    public static final int RESULT_BUST = 5;

    public static final int MAX_BET = 999;
    public static final int COIN_SLOT_LIMIT = 999;

    // --- ContainerData layout ---
    public static final int IDX_PHASE = 0;
    public static final int IDX_GAME = 1;
    public static final int IDX_BET = 2;
    public static final int IDX_RESULT = 3;
    public static final int IDX_PLAYER_COUNT = 4;
    public static final int IDX_DEALER_COUNT = 5;
    public static final int IDX_PLAYER_TOTAL = 6;
    public static final int IDX_DEALER_TOTAL = 7;
    public static final int IDX_HOLE_HIDDEN = 8;
    public static final int IDX_COINS = 9;
    public static final int IDX_PLAYER_CARDS = 10;                 // 10..21
    public static final int IDX_DEALER_CARDS = 22;                 // 22..33
    public static final int DATA_SIZE = IDX_DEALER_CARDS + Blackjack.MAX_CARDS; // 34

    public static final int CARD_EMPTY = -1;
    public static final int CARD_FACE_DOWN = -2;

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == KEY_SLOT) return stack.is(ModItems.BLACKJACK_KEY.get());
            if (slot == COIN_SLOT) return stack.is(ModItems.COIN.get());
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == COIN_SLOT ? COIN_SLOT_LIMIT : 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            onInventoryChanged();
        }
    };


    private boolean processing = false;

    private final int[] playerCards = new int[Blackjack.MAX_CARDS];
    private final int[] dealerCards = new int[Blackjack.MAX_CARDS];
    private int playerCount = 0;
    private int dealerCount = 0;
    private int phase = PHASE_BET;
    private int bet = 1;
    private int result = RESULT_NONE;

    private final int[] synced = new int[DATA_SIZE];
    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) { return synced[i]; }
        @Override public void set(int i, int v) { synced[i] = v; }
        @Override public int getCount() { return DATA_SIZE; }
    };

    public CasinoBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASINO_BE.get(), pos, state);
    }



    @Override
    public Component getDisplayName() {
        return Component.translatable("block.wastedmod.casino");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CasinoMenu(id, inv, this, this.data);
    }



    private int gameId() {
        return itemHandler.getStackInSlot(KEY_SLOT).is(ModItems.BLACKJACK_KEY.get()) ? GAME_BLACKJACK : GAME_NONE;
    }

    private int coins() {
        return itemHandler.getStackInSlot(COIN_SLOT).getCount();
    }

    private RandomSource rng() {
        return level != null ? level.getRandom() : RandomSource.create();
    }

    private void onInventoryChanged() {
        setChanged();
        if (level == null || level.isClientSide) return;
        // Skip while the casino is mid-operation (extracting bet / paying out) so the bet isn't reclamped.
        if (processing) return;

        // Key removed mid-round → refund the in-play bet straight to the coin slot.
        if (gameId() == GAME_NONE && phase == PHASE_PLAYER) {
            itemHandler.insertItem(COIN_SLOT, new ItemStack(ModItems.COIN.get(), bet), false);
            resetRound();
        }
        // Only re-clamp the chosen bet while between rounds (not during play).
        if (phase != PHASE_PLAYER) {
            bet = Mth.clamp(bet, 1, Math.max(1, Math.min(MAX_BET, coins())));
        }
        pushState();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    private void resetRound() {
        playerCount = 0;
        dealerCount = 0;
        phase = PHASE_BET;
        result = RESULT_NONE;
    }


    private void payCoins(Player player, int amount) {
        if (amount <= 0) return;
        ItemStack leftover = itemHandler.insertItem(COIN_SLOT, new ItemStack(ModItems.COIN.get(), amount), false);
        if (!leftover.isEmpty()) {
            if (!player.getInventory().add(leftover)) {
                player.drop(leftover, false);
            }
        }
    }


    private void pushState() {
        boolean hideHole = phase == PHASE_PLAYER;
        synced[IDX_PHASE] = phase;
        synced[IDX_GAME] = gameId();
        synced[IDX_BET] = bet;
        synced[IDX_RESULT] = result;
        synced[IDX_PLAYER_COUNT] = playerCount;
        synced[IDX_DEALER_COUNT] = dealerCount;
        synced[IDX_PLAYER_TOTAL] = Blackjack.handValue(playerCards, playerCount);
        synced[IDX_HOLE_HIDDEN] = hideHole ? 1 : 0;
        synced[IDX_DEALER_TOTAL] = hideHole
                ? (dealerCount > 0 ? Blackjack.handValue(dealerCards, 1) : 0)
                : Blackjack.handValue(dealerCards, dealerCount);
        synced[IDX_COINS] = coins();
        for (int i = 0; i < Blackjack.MAX_CARDS; i++) {
            synced[IDX_PLAYER_CARDS + i] = i < playerCount ? playerCards[i] : CARD_EMPTY;
            int dv = i < dealerCount ? dealerCards[i] : CARD_EMPTY;
            if (hideHole && i == 1) dv = CARD_FACE_DOWN;
            synced[IDX_DEALER_CARDS + i] = dv;
        }
        setChanged();
    }



    public void setBet(int delta) {
        if (phase == PHASE_PLAYER) return;
        int max = Math.min(MAX_BET, coins());
        if (max < 1) { bet = 0; pushState(); return; }
        bet = Mth.clamp(bet + delta, 1, max);
        pushState();
    }

    public void deal(Player player) {
        if (gameId() != GAME_BLACKJACK || phase == PHASE_PLAYER) return;
        if (bet < 1 || bet > coins()) return;

        processing = true;
        itemHandler.extractItem(COIN_SLOT, bet, false);
        resetRound();

        playerCards[playerCount++] = Blackjack.drawCard(rng());
        dealerCards[dealerCount++] = Blackjack.drawCard(rng()); // up card
        playerCards[playerCount++] = Blackjack.drawCard(rng());
        dealerCards[dealerCount++] = Blackjack.drawCard(rng()); // hole card
        phase = PHASE_PLAYER;

        boolean playerBj = Blackjack.isBlackjack(playerCards, playerCount);
        boolean dealerBj = Blackjack.isBlackjack(dealerCards, dealerCount);
        if (playerBj || dealerBj) {
            int payout;
            if (playerBj && dealerBj) { result = RESULT_PUSH; payout = bet; }
            else if (playerBj) { result = RESULT_BLACKJACK; payout = bet + (bet * 3) / 2; }
            else { result = RESULT_LOSE; payout = 0; }
            payCoins(player, payout);
            phase = PHASE_RESOLVED;
        }
        processing = false;
        pushState();
    }

    public void hit(Player player) {
        if (phase != PHASE_PLAYER || playerCount >= Blackjack.MAX_CARDS) return;
        playerCards[playerCount++] = Blackjack.drawCard(rng());
        int pv = Blackjack.handValue(playerCards, playerCount);
        if (pv > 21) {
            result = RESULT_BUST;
            phase = PHASE_RESOLVED; // no payout; reveals dealer
            pushState();
        } else if (pv == 21) {
            dealerPlayAndResolve(player);
        } else {
            pushState();
        }
    }

    public void stand(Player player) {
        if (phase != PHASE_PLAYER) return;
        dealerPlayAndResolve(player);
    }

    private void dealerPlayAndResolve(Player player) {
        while (Blackjack.handValue(dealerCards, dealerCount) < 17 && dealerCount < Blackjack.MAX_CARDS) {
            dealerCards[dealerCount++] = Blackjack.drawCard(rng());
        }
        int pv = Blackjack.handValue(playerCards, playerCount);
        int dv = Blackjack.handValue(dealerCards, dealerCount);

        int payout;
        if (dv > 21 || pv > dv) { result = RESULT_WIN; payout = bet * 2; }
        else if (pv < dv) { result = RESULT_LOSE; payout = 0; }
        else { result = RESULT_PUSH; payout = bet; }

        processing = true;
        payCoins(player, payout);
        processing = false;
        phase = PHASE_RESOLVED;
        pushState();
    }


    public void refundIfInProgress(Player player) {
        if (phase == PHASE_PLAYER) {
            processing = true;
            payCoins(player, bet);
            processing = false;
            resetRound();
            pushState();
        }
    }



    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        if (level != null) {
            Containers.dropContents(level, worldPosition, inv);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("phase", phase);
        tag.putInt("bet", bet);
        tag.putInt("result", result);
        tag.putInt("playerCount", playerCount);
        tag.putInt("dealerCount", dealerCount);
        tag.putIntArray("playerCards", playerCards.clone());
        tag.putIntArray("dealerCards", dealerCards.clone());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        phase = tag.getInt("phase");
        bet = Math.max(1, tag.getInt("bet"));
        result = tag.getInt("result");
        playerCount = tag.getInt("playerCount");
        dealerCount = tag.getInt("dealerCount");
        copyInto(playerCards, tag.getIntArray("playerCards"));
        copyInto(dealerCards, tag.getIntArray("dealerCards"));
        pushState();
    }

    private static void copyInto(int[] dest, int[] src) {
        for (int i = 0; i < dest.length; i++) dest[i] = i < src.length ? src[i] : 0;
    }
}
