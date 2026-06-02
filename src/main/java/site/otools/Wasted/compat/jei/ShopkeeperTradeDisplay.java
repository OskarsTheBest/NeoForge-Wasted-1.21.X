package site.otools.Wasted.compat.jei;

import net.minecraft.world.item.ItemStack;


public record ShopkeeperTradeDisplay(ItemStack cost, ItemStack result, int maxUses, int xp) {}
