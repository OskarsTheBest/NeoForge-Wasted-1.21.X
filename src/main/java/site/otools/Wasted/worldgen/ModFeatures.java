package site.otools.Wasted.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;

import java.util.function.Supplier;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, WastedMod.MOD_ID);

    public static final Supplier<Feature<NoneFeatureConfiguration>> TRASH_MOUNTAIN =
            FEATURES.register("trash_mountain",
                    () -> new TrashMountainFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> TRASH_PILE =
            FEATURES.register("trash_pile",
                    () -> new TrashPileFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> TRASH_HEAP =
            FEATURES.register("trash_heap",
                    () -> new TrashHeapFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
