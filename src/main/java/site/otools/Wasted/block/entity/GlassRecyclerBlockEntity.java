package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import site.otools.Wasted.item.ModItems;

public class GlassRecyclerBlockEntity extends RecyclerBlockEntity {

    public GlassRecyclerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GLASS_RECYCLER_BE.get(), pos, blockState);
    }

    @Override
    protected boolean hasRecipe() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack output = new ItemStack(ModItems.COIN.get());
        return input.is(net.neoforged.neoforge.common.Tags.Items.GLASS_BLOCKS) &&
                canInsertAmountIntoOutputSlot(output.getCount()) &&
                canInsertItemIntoOutputSlot(output);
    }
}