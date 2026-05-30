package site.otools.Wasted.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, WastedMod.MOD_ID);

    public static final Supplier<EntityType<ShopKeeperEntity>> SHOPKEEPER =
            ENTITY_TYPES.register("shopkeeper", () ->
                    EntityType.Builder.<ShopKeeperEntity>of(ShopKeeperEntity::new, MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .build("wastedmod:shopkeeper")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void registerAttributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(SHOPKEEPER.get(), net.minecraft.world.entity.monster.Zombie.createAttributes().build());
    }
}