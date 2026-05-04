package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.custom.TrashbagBlock;
import site.otools.Wasted.item.ModItems;
import site.otools.Wasted.screen.custom.MegaRecyclerMenu;

import java.util.List;

public class MegaRecyclerBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOT_COUNT = 18;
    public static final int FIRST_INPUT_SLOT = 0;
    public static final int LAST_INPUT_SLOT = 5;
    public static final int FIRST_OUTPUT_SLOT = 6;
    public static final int LAST_OUTPUT_SLOT = 17;

    public final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final ResourceKey<LootTable> TRASH_LOOT =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "blocks/recycler/trash"));
    private static final ResourceKey<LootTable> METAL_LOOT =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "blocks/recycler/metal"));
    private static final ResourceKey<LootTable> PLASTIC_LOOT =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "blocks/recycler/plastic"));
    private static final ResourceKey<LootTable> GLASS_LOOT =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "blocks/recycler/glass"));

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public MegaRecyclerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MEGA_RECYCLER_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> MegaRecyclerBlockEntity.this.progress;
                    case 1 -> MegaRecyclerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> MegaRecyclerBlockEntity.this.progress = value;
                    case 1 -> MegaRecyclerBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.wastedmod.mega_recycler");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new MegaRecyclerMenu(i, playerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.isClientSide()) return;

        // No recyclable input → arrow at 0.
        if (findInputSlot() < 0) {
            if (progress != 0) {
                resetProgress();
                setChanged(level, blockPos, blockState);
            }
            return;
        }

        if (!hasOutputSpace()) {
            if (progress != maxProgress) {
                progress = maxProgress;
                setChanged(level, blockPos, blockState);
            }
            return;
        }

        progress++;
        setChanged(level, blockPos, blockState);

        if (progress >= maxProgress) {
            craftItem();
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
    }

    private boolean hasOutputSpace() {
        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            if (itemHandler.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    /** Returns the index of the first non-empty input slot whose item is recyclable, else -1. */
    private int findInputSlot() {
        for (int i = FIRST_INPUT_SLOT; i <= LAST_INPUT_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && lootKeyFor(stack) != null) {
                return i;
            }
        }
        return -1;
    }

    private void craftItem() {
        if (level == null || level.isClientSide()) return;

        int slot = findInputSlot();
        if (slot < 0) return;

        ItemStack input = itemHandler.getStackInSlot(slot);
        ResourceKey<LootTable> lootKey = lootKeyFor(input);
        if (lootKey == null) return;

        ServerLevel serverLevel = (ServerLevel) level;
        LootTable table = serverLevel.getServer()
                .reloadableRegistries()
                .getLootTable(lootKey);

        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .create(LootContextParamSets.CHEST);

        List<ItemStack> loot = table.getRandomItems(params);
        System.out.println("[MegaRecycler] crafting from slot=" + slot + " item=" + input
                + " lootKey=" + lootKey.location() + " lootSize=" + loot.size());
        if (loot.isEmpty()) return;

        boolean insertedAny = false;
        for (ItemStack output : loot) {
            if (output.isEmpty()) continue;
            for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (stack.isEmpty()) {
                    itemHandler.setStackInSlot(i, output.copy());
                    insertedAny = true;
                    break;
                }
                if (stack.getItem() == output.getItem() &&
                        stack.getCount() + output.getCount() <= stack.getMaxStackSize()) {
                    stack.grow(output.getCount());
                    itemHandler.setStackInSlot(i, stack);
                    insertedAny = true;
                    break;
                }
            }
        }

        if (insertedAny) {
            itemHandler.extractItem(slot, 1, false);
        }
        System.out.println("[MegaRecycler] insertedAny=" + insertedAny);
    }

    private static ResourceKey<LootTable> lootKeyFor(ItemStack input) {
        if (input.isEmpty()) return null;

        if (input.is(ModItems.TRASH)) return TRASH_LOOT;

        if (input.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof TrashbagBlock) return TRASH_LOOT;
        }

        if (input.is(ModItems.PLASTIC)) return PLASTIC_LOOT;

        if (input.is(ModItems.GLASSHATTER)
                || input.is(net.neoforged.neoforge.common.Tags.Items.GLASS_BLOCKS)
                || input.is(net.neoforged.neoforge.common.Tags.Items.GLASS_PANES)) {
            return GLASS_LOOT;
        }

        if (input.is(ModItems.METAL)
                || input.is(net.neoforged.neoforge.common.Tags.Items.INGOTS)
                || input.is(net.neoforged.neoforge.common.Tags.Items.NUGGETS)
                || input.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "iron_ores")))
                || input.getItem() == Items.CHAIN
                || input.getItem() == Items.IRON_BARS) {
            return METAL_LOOT;
        }

        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("mega_recycler.progress", progress);
        pTag.putInt("mega_recycler.max_progress", maxProgress);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("mega_recycler.progress");
        maxProgress = pTag.getInt("mega_recycler.max_progress");
    }
}