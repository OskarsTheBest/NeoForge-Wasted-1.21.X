package site.otools.Wasted.compat.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record RecyclerDisplay(List<ItemStack> inputs, List<Output> outputs) {
    public record Output(ItemStack stack, float chancePercent) {}
}
