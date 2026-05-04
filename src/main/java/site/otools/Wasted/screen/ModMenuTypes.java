package site.otools.Wasted.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.screen.custom.MegaRecyclerMenu;
import site.otools.Wasted.screen.custom.RecyclerMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, WastedMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RecyclerMenu>> RECYCLER_MENU =
            registerMenuType("recycler_menu", RecyclerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<MegaRecyclerMenu>> MEGA_RECYCLER_MENU =
            registerMenuType("mega_recycler_menu", MegaRecyclerMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
