package site.otools.Wasted.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, WastedMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<GlassShatterEntity>> GLASS_SHATTER =
            ENTITIES.register("glass_shatter",
                    () -> EntityType.Builder.<GlassShatterEntity>of(GlassShatterEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("glass_shatter"));

    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeperEntity>> SHOPKEEPER =
            ENTITIES.register("shopkeeper",
                    () -> EntityType.Builder.<ShopKeeperEntity>of(ShopKeeperEntity::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(10)
                            .build("shopkeeper"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SHOPKEEPER.get(),
                Mob.createMobAttributes()
                        .add(Attributes.MAX_HEALTH, 20.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.5)
                        .add(Attributes.FOLLOW_RANGE, 48.0)
                        .build());
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
