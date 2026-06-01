package site.otools.Wasted.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.block.entity.TrashGeneratorBlockEntity;
import site.otools.Wasted.item.ModItems;


public class TrashGeneratorBlockEntityRenderer implements BlockEntityRenderer<TrashGeneratorBlockEntity> {

    private static final ResourceLocation VILLAGER_TEXTURE =
            ResourceLocation.parse("minecraft:textures/entity/villager/villager.png");


    private static final float BAG_SCALE = 0.40F;
    private static final float BAG_X = 0.5F - BAG_SCALE / 2F;
    private static final float BAG_Y = 0.05F;
    private static final float BAG_Z = 0.18F;


    private static final float V_X = 0.5F;
    private static final float V_Y = 0.05F;
    private static final float V_Z = 0.78F;
    private static final float V_SCALE = 0.40F;

    private final VillagerModel<Villager> villagerModel;
    private final ModelPart armsPart;

    public TrashGeneratorBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart root = ctx.bakeLayer(ModelLayers.VILLAGER);
        this.villagerModel = new VillagerModel<>(root);
        this.armsPart = root.getChild("arms");
    }

    @Override
    public void render(TrashGeneratorBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        if (be.getLevel() == null) return;

        final int fullLight = LightTexture.FULL_BRIGHT;
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();

        float time = be.getLevel().getGameTime() + partialTick;
        float swing01;
        boolean bagVisible;
        if (be.isFull()) {
            swing01 = 0.0F;
            bagVisible = true;
        } else {
            float t = (time * (1.0F / 60.0F)) % 1.0F;
            if (t < 0.50F) {
                swing01 = 0.0F;
                bagVisible = true;
            } else if (t < 0.58F) {
                swing01 = (t - 0.50F) / 0.08F;
                bagVisible = true;
            } else if (t < 0.83F) {
                swing01 = 1.0F;
                bagVisible = false;
            } else if (t < 0.93F) {
                swing01 = 1.0F - (t - 0.83F) / 0.17F;
                bagVisible = false;
            } else {
                swing01 = 1.0F - (t - 0.83F) / 0.17F;
                bagVisible = true;
            }
        }


        if (bagVisible) {
            BlockState bagState = ModBlocks.TRASHBAG.get().defaultBlockState();
            pose.pushPose();
            pose.translate(BAG_X, BAG_Y, BAG_Z);
            pose.scale(BAG_SCALE, BAG_SCALE, BAG_SCALE);
            blockRenderer.renderSingleBlock(bagState, pose, buffers, fullLight, overlay);
            pose.popPose();
        }


        armsPart.xRot = -1.45F + swing01 * 1.55F;

        pose.pushPose();
        pose.translate(V_X, V_Y, V_Z);
        pose.scale(V_SCALE, V_SCALE, V_SCALE);
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.translate(0.0F, -1.501F, 0.0F);

        RenderType rt = villagerModel.renderType(VILLAGER_TEXTURE);
        VertexConsumer vc = buffers.getBuffer(rt);
        villagerModel.renderToBuffer(pose, vc, fullLight, overlay);
        pose.popPose();

        if (!be.isFull()) {
            float gxA = V_X, gyA = 0.60F, gzA = 0.60F;
            float gxB = V_X, gyB = 0.20F, gzB = 0.42F;

            float gx = Mth.lerp(swing01, gxA, gxB);
            float gy = Mth.lerp(swing01, gyA, gyB);
            float gz = Mth.lerp(swing01, gzA, gzB);

            float tilt = -85F + swing01 * 115F;

            ItemStack grabber = new ItemStack(ModItems.TRASHGRABBER.get());
            pose.pushPose();
            pose.translate(gx, gy, gz);
            pose.mulPose(Axis.YP.rotationDegrees(180F));
            pose.mulPose(Axis.XP.rotationDegrees(tilt));
            pose.scale(0.35F, 0.35F, 0.35F);
            itemRenderer.renderStatic(grabber, ItemDisplayContext.FIXED, fullLight, overlay,
                    pose, buffers, be.getLevel(), 0);
            pose.popPose();
        }
    }
}
