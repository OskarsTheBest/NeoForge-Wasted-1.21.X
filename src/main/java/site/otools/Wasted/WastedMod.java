package site.otools.Wasted;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.block.custom.TrashbagBlock;
import site.otools.Wasted.block.entity.ModBlockEntities;
import site.otools.Wasted.entity.ShopKeeperRenderer;
import site.otools.Wasted.item.ModCreativeModeTabs;
import site.otools.Wasted.item.ModItems;
import site.otools.Wasted.screen.ModMenuTypes;
import site.otools.Wasted.screen.custom.MegaRecyclerScreen;
import site.otools.Wasted.screen.custom.RecyclerScreen;
import site.otools.Wasted.worldgen.ModFeatures;
import site.otools.Wasted.entity.ModEntities;
import site.otools.Wasted.fluid.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import static net.minecraft.world.item.Items.registerBlock;

@Mod(WastedMod.MOD_ID)
public class WastedMod {
    public static final String MOD_ID = "wastedmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WastedMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModEntities.register(modEventBus);
        ModFluids.register(modEventBus);
        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerCapabilities);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        LOGGER.info("ShopKeeper registered: {}", ModEntities.SHOPKEEPER);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        var cap = net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK;
        event.registerBlockEntity(cap, ModBlockEntities.RECYCLER_BE.get(), (be, side) -> be.getSidedItemHandler());
        event.registerBlockEntity(cap, ModBlockEntities.GLASS_RECYCLER_BE.get(), (be, side) -> be.getSidedItemHandler());
        event.registerBlockEntity(cap, ModBlockEntities.METAL_RECYCLER_BE.get(), (be, side) -> be.getSidedItemHandler());
        event.registerBlockEntity(cap, ModBlockEntities.PLASTIC_RECYCLER_BE.get(), (be, side) -> be.getSidedItemHandler());
        event.registerBlockEntity(cap, ModBlockEntities.MEGA_RECYCLER_BE.get(), (be, side) -> be.getSidedItemHandler());
        event.registerBlockEntity(cap, ModBlockEntities.TRASH_GENERATOR_BE.get(), (be, side) -> be.getSidedItemHandler());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
       /*
       if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.TRASH);
        }
       */
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = WastedMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public int getTintColor() {
                    return 0xCC4E7A2A;
                }
                @Override
                public ResourceLocation getStillTexture() {
                    return ResourceLocation.withDefaultNamespace("block/water_still");
                }
                @Override
                public ResourceLocation getFlowingTexture() {
                    return ResourceLocation.withDefaultNamespace("block/water_flow");
                }
                @Override
                public ResourceLocation getOverlayTexture() {
                    return ResourceLocation.withDefaultNamespace("block/water_overlay");
                }
            }, ModFluids.POLLUTED_WATER_TYPE.get());
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.RECYCLER_MENU.get(), RecyclerScreen::new);
            event.register(ModMenuTypes.MEGA_RECYCLER_MENU.get(), MegaRecyclerScreen::new);
            event.register(ModMenuTypes.CASINO_MENU.get(), site.otools.Wasted.screen.custom.CasinoScreen::new);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(
                    ModEntities.GLASS_SHATTER.get(),
                    net.minecraft.client.renderer.entity.ThrownItemRenderer::new
            );
            event.registerEntityRenderer(ModEntities.SHOPKEEPER.get(), ShopKeeperRenderer::new);

            event.registerBlockEntityRenderer(
                    site.otools.Wasted.block.entity.ModBlockEntities.TRASH_GENERATOR_BE.get(),
                    site.otools.Wasted.block.entity.renderer.TrashGeneratorBlockEntityRenderer::new
            );
        }
    }
}