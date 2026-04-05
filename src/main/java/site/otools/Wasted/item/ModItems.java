package site.otools.Wasted.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.ModBlocks;
import net.minecraft.world.item.BlockItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WastedMod.MOD_ID);

    public static final DeferredItem<Item> TRASH = ITEMS.register("trash", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COIN = ITEMS.register("coin", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GLASS = ITEMS.register("glass", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> METAL = ITEMS.register("metal", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TRASHBAG_V1 = ITEMS.register("trashbag_v1",
            () -> new BlockItem(ModBlocks.TRASHBAG_V1.get(), new Item.Properties()));

    public static final DeferredItem<Item> TRASHBAG_V2 = ITEMS.register("trashbag_v2",
            () -> new BlockItem(ModBlocks.TRASHBAG_V2.get(), new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
