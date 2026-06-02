package site.otools.Wasted.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import site.otools.Wasted.WastedMod;

/** JEI recipe types added by this mod. */
public final class WastedRecipeTypes {
    public static final RecipeType<RecyclerDisplay> GLASS_RECYCLING =
            RecipeType.create(WastedMod.MOD_ID, "glass_recycling", RecyclerDisplay.class);
    public static final RecipeType<RecyclerDisplay> METAL_RECYCLING =
            RecipeType.create(WastedMod.MOD_ID, "metal_recycling", RecyclerDisplay.class);
    public static final RecipeType<RecyclerDisplay> PLASTIC_RECYCLING =
            RecipeType.create(WastedMod.MOD_ID, "plastic_recycling", RecyclerDisplay.class);
    public static final RecipeType<RecyclerDisplay> TRASH_RECYCLING =
            RecipeType.create(WastedMod.MOD_ID, "trash_recycling", RecyclerDisplay.class);
    public static final RecipeType<ShopkeeperTradeDisplay> TRADES =
            RecipeType.create(WastedMod.MOD_ID, "shopkeeper_trades", ShopkeeperTradeDisplay.class);

    private WastedRecipeTypes() {}
}
