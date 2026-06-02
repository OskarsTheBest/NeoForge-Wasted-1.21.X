package site.otools.Wasted.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import site.otools.Wasted.item.ModItems;

public class ShopkeeperTradeCategory implements IRecipeCategory<ShopkeeperTradeDisplay> {
    private static final int WIDTH = 140;
    private static final int HEIGHT = 40;

    private final Component title;
    private final IDrawable icon;

    public ShopkeeperTradeCategory(IGuiHelper guiHelper) {
        this.title = Component.translatable("gui.wastedmod.jei.trades");
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.SHOPKEEPER_SPAWN_EGG.get()));
    }

    @Override
    public RecipeType<ShopkeeperTradeDisplay> getRecipeType() {
        return WastedRecipeTypes.TRADES;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShopkeeperTradeDisplay recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 12).addItemStack(recipe.cost());
        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 22, 12).addItemStack(recipe.result());
    }

    @Override
    public void draw(ShopkeeperTradeDisplay recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, "→", WIDTH / 2 - 4, 16, 0xFF555555, false);
        Component uses = Component.translatable("gui.wastedmod.jei.max_uses", recipe.maxUses());
        guiGraphics.drawString(font, uses, 6, 2, 0xFF555555, false);
    }
}
