package site.otools.Wasted.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.ModBlocks;


public class RecyclerScreen extends AbstractContainerScreen<RecyclerMenu> {

    private ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID,"textures/gui/recycler/classic_recycler_gui.png");

    @Override
    protected void init() {
        super.init();

        var block = menu.getBlockEntity().getBlockState().getBlock();

        if (block == ModBlocks.RECYCLER.get()) {
            GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID,"textures/gui/recycler/classic_recycler_gui.png");
        } else if (block == ModBlocks.GLASS_RECYCLER.get()) {
            GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID,"textures/gui/recycler/glass_recycler_gui.png");
        } else if (block == ModBlocks.METAL_RECYCLER.get()) {
            GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID,"textures/gui/recycler/metal_recycler_gui.png");
        } else if (block == ModBlocks.PLASTIC_RECYCLER.get()) {
            GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID,"textures/gui/recycler/plastic_recycler_gui.png");
        }
    }


    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID,"textures/gui/arrow_progress.png");

    public RecyclerScreen(RecyclerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(ARROW_TEXTURE,x + 59, y + 35, 0, 0, menu.getScaledArrowProgress(), 16, 24, 16);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
