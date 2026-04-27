package site.otools.Wasted.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.ModBlocks;
import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, WastedMod.MOD_ID);

    public static final Supplier<BlockEntityType<RecyclerBlockEntity>> RECYCLER_BE =
            BLOCK_ENTITIES.register("recycler_be", () -> BlockEntityType.Builder.of(
                    RecyclerBlockEntity::new, ModBlocks.RECYCLER.get()).build(null));

    public static final Supplier<BlockEntityType<GlassRecyclerBlockEntity>> GLASS_RECYCLER_BE =
            BLOCK_ENTITIES.register("glass_recycler_be", () -> BlockEntityType.Builder.of(
                    GlassRecyclerBlockEntity::new, ModBlocks.GLASS_RECYCLER.get()).build(null));

    public static final Supplier<BlockEntityType<MetalRecyclerBlockEntity>> METAL_RECYCLER_BE =
            BLOCK_ENTITIES.register("metal_recycler_be", () -> BlockEntityType.Builder.of(
                    MetalRecyclerBlockEntity::new, ModBlocks.METAL_RECYCLER.get()).build(null));

    public static final Supplier<BlockEntityType<MetalRecyclerBlockEntity>> PLASTIC_RECYCLER_BE =
            BLOCK_ENTITIES.register("plastic_recycler_be", () -> BlockEntityType.Builder.of(
                    MetalRecyclerBlockEntity::new, ModBlocks.PLASTIC_RECYCLER.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
