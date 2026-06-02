package site.otools.Wasted.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class RecyclerRecipeCategory implements IRecipeCategory<RecyclerDisplay> {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 80;
    private static final int COLS = 5;

    private final RecipeType<RecyclerDisplay> type;
    private final Component title;
    private final IDrawable icon;

    public RecyclerRecipeCategory(IGuiHelper guiHelper, RecipeType<RecyclerDisplay> type,
                                  String titleKey, ItemLike iconItem) {
        this.type = type;
        this.title = Component.translatable(titleKey);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(iconItem));
    }

    @Override
    public RecipeType<RecyclerDisplay> getRecipeType() {
        return type;
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecyclerDisplay recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, HEIGHT / 2 - 9)
                .addItemStacks(recipe.inputs());

        int startX = 46;
        int startY = 4;
        int col = 0;
        int row = 0;
        for (RecyclerDisplay.Output o : recipe.outputs()) {
            int x = startX + col * 18;
            int y = startY + row * 18;
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(o.stack())
                    .addRichTooltipCallback((slotView, tooltip) ->
                            tooltip.add(Component.translatable(
                                    "gui.wastedmod.jei.chance",
                                    String.format("%.1f", o.chancePercent()))));
            if (++col >= COLS) {
                col = 0;
                row++;
            }
        }
    }
}
