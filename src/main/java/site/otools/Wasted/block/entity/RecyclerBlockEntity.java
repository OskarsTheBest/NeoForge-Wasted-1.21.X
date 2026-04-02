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

    public final ItemStackHandler itemHandler= new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    protected static final int INPUT_SLOT = 0;
    protected static final int OUTPUT_SLOT = 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public RecyclerBlockEntity(BlockPos pos, BlockState blockState){
        this(ModBlockEntities.RECYCLER_BE.get(), pos, blockState);
    }

    protected RecyclerBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState blockState){
        super(type, pos, blockState);
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
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
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


    protected boolean hasRecipe() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack output = new ItemStack(ModItems.COIN.get());

        return (input.is(ModItems.TRASH) || input.is(ModItems.GLASS) || input.is(ModItems.METAL)) &&
                canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    protected boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    protected boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
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
