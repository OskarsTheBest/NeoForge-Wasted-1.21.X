package site.otools.Wasted.entity;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import site.otools.Wasted.WastedMod;

public class ShopKeeperRenderer extends MobRenderer<ShopKeeperEntity, VillagerModel<ShopKeeperEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "minecraft", "textures/entity/villager/villager.png");

    public ShopKeeperRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new VillagerModel<>(ctx.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ShopKeeperEntity entity) {
        return TEXTURE;
    }
}