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
import site.otools.Wasted.item.ModCreativeModeTabs;
import site.otools.Wasted.item.ModItems;
import site.otools.Wasted.screen.ModMenuTypes;
import site.otools.Wasted.screen.custom.RecyclerScreen;

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
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

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
    @EventBusSubscriber(modid = WastedMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {

        }
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.RECYCLER_MENU.get(), RecyclerScreen::new);
        }
    }

}
