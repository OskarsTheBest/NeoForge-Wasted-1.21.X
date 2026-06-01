package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import site.otools.Wasted.item.ModItems;


public class TrashGeneratorBlockEntity extends BlockEntity {

    /** Ticks between trash generations. 200 = 10 seconds at 20 TPS. */
    public static final int GENERATION_INTERVAL_TICKS = 200;
    public static final int MAX_STORAGE = 64;

    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return MAX_STORAGE;
        }
    };

    private final IItemHandler outputView = new IItemHandler() {
        @Override public int getSlots() { return itemHandler.getSlots(); }
        @Override public @NotNull ItemStack getStackInSlot(int slot) { return itemHandler.getStackInSlot(slot); }
        @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
        @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return itemHandler.extractItem(slot, amount, simulate);
        }
        @Override public int getSlotLimit(int slot) { return itemHandler.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return false; }
    };

    private int progress = 0;

    public TrashGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TRASH_GENERATOR_BE.get(), pos, blockState);
    }

    public IItemHandler getSidedItemHandler() {
        return outputView;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isFull() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        return !stack.isEmpty() && stack.getCount() >= MAX_STORAGE;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;


        if (!isFull() && level instanceof net.minecraft.server.level.ServerLevel sl
                && level.getGameTime() % 60L == 35L) {
            double px = pos.getX() + 0.5;
            double py = pos.getY() + 0.22;
            double pz = pos.getZ() + 0.30;
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
                    px, py, pz,
                    6,
                    0.10, 0.05, 0.10,
                    0.02);
        }

        if (isFull()) return;

        progress++;
        if (progress >= GENERATION_INTERVAL_TICKS) {
            progress = 0;
            ItemStack current = itemHandler.getStackInSlot(0);
            if (current.isEmpty()) {
                itemHandler.setStackInSlot(0, new ItemStack(ModItems.TRASH.get(), 1));
            } else if (current.is(ModItems.TRASH.get()) && current.getCount() < MAX_STORAGE) {
                current.grow(1);
                itemHandler.setStackInSlot(0, current);
            }
            setChanged(level, pos, state);
        }
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, inv);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
    }
}
