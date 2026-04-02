package site.otools.Wasted.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WastedMod.MOD_ID);

    public static final Supplier<CreativeModeTab> WASTED_ITEMS_TAB = CREATIVE_MODE_TAB.register("wasted_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TRASH.get()))
                    .title(Component.translatable("creativetab.wastedmod.wasted_items"))
                    .displayItems((ItemDisplayParameters, output) ->{

                        output.accept(ModItems.TRASH);
                        output.accept(ModItems.COIN);

                        output.accept(ModBlocks.TRASHBAG.get());
                        output.accept(ModBlocks.RECYCLER.get());
                        output.accept(ModBlocks.GLASS_RECYCLER.get());
                        output.accept(ModBlocks.METAL_RECYCLER.get());
                        //output.accept(ModItems.xxxx);
                    })
                    .build());

    public static void register(IEventBus eventbus) {
        CREATIVE_MODE_TAB.register(eventbus);
    }
}
