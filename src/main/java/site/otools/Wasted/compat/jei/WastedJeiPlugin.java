package site.otools.Wasted.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import site.otools.Wasted.fluid.ModFluids;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import site.otools.Wasted.Config;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.item.ModItems;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class WastedJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper g = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new RecyclerRecipeCategory(g, WastedRecipeTypes.GLASS_RECYCLING,
                        "gui.wastedmod.jei.recycling.glass", ModBlocks.GLASS_RECYCLER.get()),
                new RecyclerRecipeCategory(g, WastedRecipeTypes.METAL_RECYCLING,
                        "gui.wastedmod.jei.recycling.metal", ModBlocks.METAL_RECYCLER.get()),
                new RecyclerRecipeCategory(g, WastedRecipeTypes.PLASTIC_RECYCLING,
                        "gui.wastedmod.jei.recycling.plastic", ModBlocks.PLASTIC_RECYCLER.get()),
                new RecyclerRecipeCategory(g, WastedRecipeTypes.TRASH_RECYCLING,
                        "gui.wastedmod.jei.recycling.trash", ModBlocks.RECYCLER.get()),
                new ShopkeeperTradeCategory(g)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(WastedRecipeTypes.GLASS_RECYCLING,
                List.of(new RecyclerDisplay(glassInputs(), LootJsonReader.read("glass"))));
        registration.addRecipes(WastedRecipeTypes.METAL_RECYCLING,
                List.of(new RecyclerDisplay(metalInputs(), LootJsonReader.read("metal"))));
        registration.addRecipes(WastedRecipeTypes.PLASTIC_RECYCLING,
                List.of(new RecyclerDisplay(plasticInputs(), LootJsonReader.read("plastic"))));
        registration.addRecipes(WastedRecipeTypes.TRASH_RECYCLING,
                List.of(new RecyclerDisplay(trashInputs(), LootJsonReader.read("trash"))));

        registration.addRecipes(WastedRecipeTypes.TRADES, trades());

        // Info pages ("?" tab in JEI) documenting non-recipe ways trash is produced.
        registration.addIngredientInfo(new ItemStack(ModItems.TRASH.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("gui.wastedmod.jei.info.trash"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.TRASHBAG.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("gui.wastedmod.jei.info.trashbag"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.TRASH_GENERATOR.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("gui.wastedmod.jei.info.trash_generator"));
        registration.addIngredientInfo(
                new FluidStack(ModFluids.POLLUTED_WATER.get(), FluidType.BUCKET_VOLUME),
                NeoForgeTypes.FLUID_STACK,
                Component.translatable("gui.wastedmod.jei.info.polluted_water"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GLASS_RECYCLER.get()),
                WastedRecipeTypes.GLASS_RECYCLING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.METAL_RECYCLER.get()),
                WastedRecipeTypes.METAL_RECYCLING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PLASTIC_RECYCLER.get()),
                WastedRecipeTypes.PLASTIC_RECYCLING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RECYCLER.get()),
                WastedRecipeTypes.TRASH_RECYCLING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MEGA_RECYCLER.get()),
                WastedRecipeTypes.GLASS_RECYCLING, WastedRecipeTypes.METAL_RECYCLING,
                WastedRecipeTypes.PLASTIC_RECYCLING, WastedRecipeTypes.TRASH_RECYCLING);
        registration.addRecipeCatalyst(new ItemStack(ModItems.SHOPKEEPER_SPAWN_EGG.get()),
                WastedRecipeTypes.TRADES);
    }



    private static List<ItemStack> glassInputs() {
        List<ItemStack> l = new ArrayList<>();
        l.add(new ItemStack(ModItems.GLASSHATTER.get()));
        addTag(l, Tags.Items.GLASS_BLOCKS);
        addTag(l, Tags.Items.GLASS_PANES);
        return l;
    }

    private static List<ItemStack> metalInputs() {
        List<ItemStack> l = new ArrayList<>();
        l.add(new ItemStack(ModItems.METAL.get()));
        addTag(l, Tags.Items.INGOTS);
        addTag(l, Tags.Items.NUGGETS);
        addTag(l, ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "iron_ores")));
        l.add(new ItemStack(Items.CHAIN));
        l.add(new ItemStack(Items.IRON_BARS));
        return l;
    }

    private static List<ItemStack> plasticInputs() {
        List<ItemStack> l = new ArrayList<>();
        l.add(new ItemStack(ModItems.PLASTIC.get()));
        return l;
    }

    private static List<ItemStack> trashInputs() {
        List<ItemStack> l = new ArrayList<>();
        l.add(new ItemStack(ModItems.TRASH.get()));
        l.add(new ItemStack(ModBlocks.TRASHBAG_BLOCK.get()));
        return l;
    }

    private static void addTag(List<ItemStack> list, TagKey<Item> tag) {
        for (ItemStack stack : Ingredient.of(tag).getItems()) {
            list.add(stack);
        }
    }

    private static List<ShopkeeperTradeDisplay> trades() {
        List<ShopkeeperTradeDisplay> l = new ArrayList<>();
        try {
            for (Config.TradeEntry t : Config.getParsedTrades()) {
                Item cost = BuiltInRegistries.ITEM.get(ResourceLocation.parse(t.costItem()));
                Item result = BuiltInRegistries.ITEM.get(ResourceLocation.parse(t.resultItem()));
                l.add(new ShopkeeperTradeDisplay(
                        new ItemStack(cost, t.costAmount()),
                        new ItemStack(result, t.resultAmount()),
                        t.maxUses(), t.xp()));
            }
        } catch (Exception ignored) {

        }
        return l;
    }
}
