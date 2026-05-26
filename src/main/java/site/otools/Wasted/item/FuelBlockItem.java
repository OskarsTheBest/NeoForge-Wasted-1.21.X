package site.otools.Wasted.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

/** A BlockItem that can be burned as furnace fuel for a fixed number of ticks. */
public class FuelBlockItem extends BlockItem {
    private final int burnTimeTicks;

    public FuelBlockItem(Block block, Properties properties, int burnTimeTicks) {
        super(block, properties);
        this.burnTimeTicks = burnTimeTicks;
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return burnTimeTicks;
    }
}
