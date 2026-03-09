package site.otools.Wasted.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WastedMod.MOD_ID);

    public static final DeferredItem<Item> TRASH = ITEMS.register("trash", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
