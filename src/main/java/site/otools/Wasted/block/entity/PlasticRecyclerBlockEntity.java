package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.item.ModItems;
import site.otools.Wasted.screen.custom.RecyclerMenu;

import javax.annotation.Nullable;
import java.util.List;

public class PlasticRecyclerBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(13) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    protected static final int INPUT_SLOT = 0;
    protected static final int FIRST_OUTPUT_SLOT = 1;
    protected static final int LAST_OUTPUT_SLOT = 12;
    private static final ResourceKey<LootTable> RECYCLER_LOOT =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "blocks/recycler/plastic"));

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    public PlasticRecyclerBlockEntity(BlockPos pos, BlockState blockState) {
        this(ModBlockEntities.PLASTIC_RECYCLER_BE.get(), pos, blockState);
    }
    protected PlasticRecyclerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> PlasticRecyclerBlockEntity.this.progress;
                    case 1 -> PlasticRecyclerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }
            @Override
            public void set(int i, int value) {
                switch(i){
                    case 0 -> PlasticRecyclerBlockEntity.this.progress = value;
                    case 1 -> PlasticRecyclerBlockEntity.this.maxProgress = value;

                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }
    @Override
    public Component getDisplayName() {return Component.translatable("block.wastedmod.plastic_recycler");}

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player){
        return new RecyclerMenu(i, playerInventory,this,this.data);
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
    protected void craftItem() {
        if (level == null || level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        LootTable table = serverLevel.getServer()
                .reloadableRegistries()
                .getLootTable(RECYCLER_LOOT);

        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .create(LootContextParamSets.CHEST);

        List<ItemStack> loot = table.getRandomItems(params);

        System.out.println("Loot size: " + loot.size());
        for (int idx = 0; idx < loot.size(); idx++) {
            ItemStack stack = loot.get(idx);
            System.out.println("Loot[" + idx + "]=" + stack);
        }

        if (loot.isEmpty()) return;

        boolean insertedAny = false;
        for (ItemStack output : loot) {
            if (output.isEmpty()) continue; // Loot tables can legally return empty stacks; don't "consume" for those.

            for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);

                if (stack.isEmpty()) {
                    if (!output.copy().isEmpty()) {
                        itemHandler.setStackInSlot(i, output.copy());
                        insertedAny = true;
                    }
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

        // Only consume the input if we actually managed to insert something.
        if (insertedAny) {
            itemHandler.extractItem(INPUT_SLOT, 1, false);
        }

        System.out.println("InsertedAny=" + insertedAny);
        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            ItemStack s = itemHandler.getStackInSlot(i);
            if (!s.isEmpty()) {
                System.out.println("OutputSlot[" + i + "]=" + s);
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


    protected boolean hasRecipe() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);

        boolean isPlasticItem =
                input.getItem() == ModItems.PLASTIC.get();

        if (!isPlasticItem) return false;

        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) return true;
            if (stack.getCount() < stack.getMaxStackSize()) return true;
        }
        return false;
    }
    protected boolean canInsertItemIntoOutputSlot(ItemStack output) {
        if (!itemHandler.getStackInSlot(INPUT_SLOT).is(ModItems.PLASTIC))
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

    protected boolean canInsertAmountIntoOutputSlot(int count) {
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














