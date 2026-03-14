package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.item.ModItems;
import site.otools.Wasted.screen.custom.RecyclerMenu;

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler= new ItemStackHandler(13){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private static final int INPUT_SLOT = 0;
    private static final int FIRST_OUTPUT_SLOT = 1;
    private static final int LAST_OUTPUT_SLOT = 12;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;


    public RecyclerBlockEntity(BlockPos pos, BlockState blockState){
        super(ModBlockEntities.RECYCLER_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> RecyclerBlockEntity.this.progress;
                    case 1 -> RecyclerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch(i){
                    case 0 -> RecyclerBlockEntity.this.progress = value;
                    case 1 -> RecyclerBlockEntity.this.maxProgress = value;

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
        return Component.translatable("block.wasted.recycler");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new RecyclerMenu(i, playerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < inventory.getContainerSize(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    //gets called every tick / 20times in 1 sec
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if(hasRecipe()){
            increaseCraftingProgress();
            setChanged(level, blockPos, blockState);

            if(hasCraftingFinished()){
                craftItem();
                resetProgress();
            }

        }else {
            resetProgress();
        }

    }

    private void craftItem() {
        ItemStack output = new ItemStack(ModItems.COIN.get(),1);

        itemHandler.extractItem(INPUT_SLOT, 1, false);

        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if (stack.isEmpty()) {
                itemHandler.setStackInSlot(i, output.copy());
                return;
            }

            if (stack.getItem() == output.getItem() && stack.getCount() < stack.getMaxStackSize()) {
                stack.grow(output.getCount());
                itemHandler.setStackInSlot(i, stack);
                return;
            }
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }


    private boolean hasRecipe() {
        ItemStack output = new ItemStack(ModItems.COIN.get()); // hardcoded , can be loot table with diff items
        // input must be trash
        if (!itemHandler.getStackInSlot(INPUT_SLOT).is(ModItems.TRASH)) {
            return false;
        }

        // check if ANY output slot can accept the item
        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if (stack.isEmpty()) {
                return true;
            }

            if (stack.getItem() == output.getItem() &&
                    stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }

        return false;
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        if (!itemHandler.getStackInSlot(INPUT_SLOT).is(ModItems.TRASH))
            return false;

        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if (stack.isEmpty())
                return true;

            if (stack.getItem() == output.getItem() &&
                    stack.getCount() + output.getCount() <= stack.getMaxStackSize())
                return true;
        }

        return false;
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            int maxCount = stack.isEmpty() ? 64 : stack.getMaxStackSize();
            int currentCount = stack.getCount();

            if (maxCount >= currentCount + count) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("recycler.progress", progress);
        pTag.putInt("recycler.max_progress", maxProgress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("recycler.progress");
        maxProgress = pTag.getInt("recycler.max_progress");
    }



}
