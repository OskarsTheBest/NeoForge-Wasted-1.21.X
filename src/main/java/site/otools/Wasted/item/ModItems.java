package site.otools.Wasted.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import site.otools.Wasted.entity.ModEntities;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WastedMod.MOD_ID);

    public static final DeferredItem<Item> TRASHGRABBER = ITEMS.register("trashgrabber", () -> new Item(
            new Item.Properties()
                    .component(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                            .add(Attributes.BLOCK_INTERACTION_RANGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "trashgrabber_reach"),
                                            2.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build())));
    public static final DeferredItem<Item> TRASH = ITEMS.register("trash", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COIN = ITEMS.register("coin", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GLASSHATTER = ITEMS.register("glasshatter", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> METAL = ITEMS.register("metal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PLASTIC = ITEMS.register("plastic", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TRASHBAG_V1 = ITEMS.register("trashbag_v1",
            () -> new BlockItem(ModBlocks.TRASHBAG_V1.get(), new Item.Properties()));

    public static final DeferredItem<Item> TRASHBAG_V2 = ITEMS.register("trashbag_v2",
            () -> new BlockItem(ModBlocks.TRASHBAG_V2.get(), new Item.Properties()));

    public static final DeferredItem<Item> SHOPKEEPER_SPAWN_EGG = ITEMS.register("shopkeeper_spawn_egg",
            () -> new net.minecraft.world.item.SpawnEggItem(
                    ModEntities.SHOPKEEPER.get(),
                    0x2C2C2C, 0xFFD700,
                    new Item.Properties()
            ));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
