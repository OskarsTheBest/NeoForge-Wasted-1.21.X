package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import site.otools.Wasted.item.ModItems;

public class MetalRecyclerBlockEntity extends RecyclerBlockEntity {

    public MetalRecyclerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.METAL_RECYCLER_BE.get(), pos, blockState);
    }

    @Override
    protected boolean hasRecipe() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack output = new ItemStack(ModItems.COIN.get());
        return (input.is(net.neoforged.neoforge.common.Tags.Items.INGOTS) ||
                input.is(net.neoforged.neoforge.common.Tags.Items.NUGGETS) ||
                input.is(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "iron_ores"))) ||
                input.getItem() == net.minecraft.world.item.Items.CHAIN ||
                input.getItem() == net.minecraft.world.item.Items.IRON_BARS) &&
                canInsertAmountIntoOutputSlot(output.getCount()) &&
                canInsertItemIntoOutputSlot(output);
    }
}