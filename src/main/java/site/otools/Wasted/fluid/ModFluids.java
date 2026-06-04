package site.otools.Wasted.fluid;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.item.ModItems;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, WastedMod.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, WastedMod.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> POLLUTED_WATER_TYPE =
            FLUID_TYPES.register("polluted_water", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("block.wastedmod.polluted_water")
                            .canSwim(true)
                            .canDrown(true)
                            .canHydrate(false)
                            .canConvertToSource(false)
                            .density(1000)
                            .viscosity(1000)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> POLLUTED_WATER =
            FLUIDS.register("polluted_water", () -> new BaseFlowingFluid.Source(ModFluids.POLLUTED_WATER_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> POLLUTED_WATER_FLOWING =
            FLUIDS.register("polluted_water_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.POLLUTED_WATER_PROPERTIES));

    public static final BaseFlowingFluid.Properties POLLUTED_WATER_PROPERTIES =
            new BaseFlowingFluid.Properties(POLLUTED_WATER_TYPE, POLLUTED_WATER, POLLUTED_WATER_FLOWING)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(1)
                    .block(ModBlocks.POLLUTED_WATER_BLOCK)
                    .bucket(ModItems.POLLUTED_WATER_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
