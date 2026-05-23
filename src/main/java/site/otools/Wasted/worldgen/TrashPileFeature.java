package site.otools.Wasted.worldgen;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.neoforge.common.ModConfigSpec;
import site.otools.Wasted.Config;
import site.otools.Wasted.block.ModBlocks;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TrashPileFeature extends Feature<NoneFeatureConfiguration> {

    private enum Placement { SURFACE_LAND, OCEAN_FLOOR, UNDERGROUND }

    private record Target(ModConfigSpec.DoubleValue chance, Placement placement, boolean guaranteedInStart) {}

    private record KeyTarget(ResourceKey<Structure> key, ModConfigSpec.DoubleValue chance,
                             Placement placement, boolean guaranteedInStart) {}


    private static List<KeyTarget> keyTargets() {
        return List.of(
                new KeyTarget(BuiltinStructures.MINESHAFT, Config.TRASH_PILE_MINESHAFT, Placement.UNDERGROUND, false),
                new KeyTarget(BuiltinStructures.MINESHAFT_MESA, Config.TRASH_PILE_MINESHAFT, Placement.UNDERGROUND, false),
                new KeyTarget(BuiltinStructures.STRONGHOLD, Config.TRASH_PILE_STRONGHOLD, Placement.UNDERGROUND, false),
                new KeyTarget(BuiltinStructures.TRAIL_RUINS, Config.TRASH_PILE_TRAIL_RUINS, Placement.SURFACE_LAND, false),
                new KeyTarget(BuiltinStructures.JUNGLE_TEMPLE, Config.TRASH_PILE_JUNGLE_TEMPLE, Placement.SURFACE_LAND, true),
                new KeyTarget(BuiltinStructures.VILLAGE_PLAINS, Config.TRASH_PILE_VILLAGE, Placement.SURFACE_LAND, false),
                new KeyTarget(BuiltinStructures.VILLAGE_DESERT, Config.TRASH_PILE_VILLAGE, Placement.SURFACE_LAND, false),
                new KeyTarget(BuiltinStructures.VILLAGE_SAVANNA, Config.TRASH_PILE_VILLAGE, Placement.SURFACE_LAND, false),
                new KeyTarget(BuiltinStructures.VILLAGE_SNOWY, Config.TRASH_PILE_VILLAGE, Placement.SURFACE_LAND, false),
                new KeyTarget(BuiltinStructures.VILLAGE_TAIGA, Config.TRASH_PILE_VILLAGE, Placement.SURFACE_LAND, false),
                new KeyTarget(BuiltinStructures.BASTION_REMNANT, Config.TRASH_PILE_BASTION, Placement.UNDERGROUND, false),
                new KeyTarget(BuiltinStructures.OCEAN_RUIN_COLD, Config.TRASH_PILE_OCEAN_RUINS, Placement.OCEAN_FLOOR, false),
                new KeyTarget(BuiltinStructures.OCEAN_RUIN_WARM, Config.TRASH_PILE_OCEAN_RUINS, Placement.OCEAN_FLOOR, false)
        );
    }


    private static final java.util.Set<Block> GROUND_BLOCKS = java.util.Set.of(
            Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.ROOTED_DIRT,
            Blocks.DIRT_PATH, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.STONE,
            Blocks.COBBLESTONE, Blocks.MUD, Blocks.PACKED_MUD, Blocks.MOSS_BLOCK,
            Blocks.SNOW_BLOCK, Blocks.MYCELIUM
    );

    public TrashPileFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        ChunkAccess chunk = level.getChunk(origin);


        java.util.Set<Structure> starts = new java.util.HashSet<>();
        for (Map.Entry<Structure, StructureStart> e : chunk.getAllStarts().entrySet()) {
            if (e.getValue() != null && e.getValue().isValid()) starts.add(e.getKey());
        }

        java.util.Set<Structure> present = new java.util.HashSet<>(starts);
        for (Map.Entry<Structure, LongSet> e : chunk.getAllReferences().entrySet()) {
            if (!e.getValue().isEmpty()) present.add(e.getKey());
        }
        if (present.isEmpty()) return false;

        HolderLookup.RegistryLookup<Structure> lookup =
                level.registryAccess().lookupOrThrow(Registries.STRUCTURE);


        Map<Structure, Target> byValue = new IdentityHashMap<>();
        for (KeyTarget kt : keyTargets()) {
            lookup.get(kt.key()).ifPresent(holder ->
                    byValue.put(holder.value(), new Target(kt.chance(), kt.placement(), kt.guaranteedInStart())));
        }

        for (Structure structure : present) {
            Target target = byValue.get(structure);
            if (target == null) continue;

            boolean forced = target.guaranteedInStart() && starts.contains(structure);
            float chance = forced ? 1.0f : target.chance().get().floatValue();
            if (random.nextFloat() > chance) continue;

            return placeCluster(level, origin, random, target.placement());
        }
        return false;
    }

    private boolean placeCluster(WorldGenLevel level, BlockPos origin, RandomSource random, Placement placement) {
        BlockPos center = findFloor(level, origin.getX(), origin.getZ(), origin.getY(), placement);
        if (center == null) return false;

        int wanted = 2 + random.nextInt(4);
        int placed = 0;
        for (int attempt = 0; attempt < 24 && placed < wanted; attempt++) {

            int x = center.getX() + random.nextInt(3) - 1;
            int z = center.getZ() + random.nextInt(3) - 1;

            BlockPos floor;
            if (placement == Placement.UNDERGROUND) {
                floor = findOpenFloorNear(level, x, center.getY(), z);
            } else {
                floor = findFloor(level, x, z, center.getY(), placement);
            }
            if (floor == null) continue;

            if (placeTrashbag(level, floor, random)) placed++;
        }
        return placed > 0;
    }

    /** Finds the spot a trashbag should sit  */
    private BlockPos findFloor(WorldGenLevel level, int x, int z, int startY, Placement placement) {
        switch (placement) {
            case SURFACE_LAND -> {

                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                int bottom = level.getMinBuildHeight() + 1;
                for (int y = top; y > bottom; y--) {
                    BlockPos at = new BlockPos(x, y, z);
                    if (GROUND_BLOCKS.contains(level.getBlockState(at.below()).getBlock())
                            && isReplaceable(level, at)) {
                        return at;
                    }
                }
                return null;
            }
            case OCEAN_FLOOR -> {
                int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                BlockPos at = new BlockPos(x, y, z);
                if (!isSturdyBelow(level, at)) return null;
                return at;
            }
            default -> {
                int top = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 5;
                int bottom = level.getMinBuildHeight() + 5;
                for (int y = top; y > bottom; y--) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (isOpenFloor(level, p)) return p;
                }
                return null;
            }
        }
    }

    private BlockPos findOpenFloorNear(WorldGenLevel level, int x, int centerY, int z) {
        for (int dy = 0; dy <= 4; dy++) {
            BlockPos up = new BlockPos(x, centerY + dy, z);
            if (isOpenFloor(level, up)) return up;
            BlockPos down = new BlockPos(x, centerY - dy, z);
            if (isOpenFloor(level, down)) return down;
        }
        return null;
    }

    private boolean placeTrashbag(WorldGenLevel level, BlockPos pos, RandomSource random) {
        if (!isReplaceable(level, pos)) return false;
        if (!isSturdyBelow(level, pos)) return false;

        Block[] bagPalette = {
                ModBlocks.TRASHBAG.get(), ModBlocks.TRASHBAG_V1.get(), ModBlocks.TRASHBAG_V2.get()
        };
        Block bag = bagPalette[random.nextInt(bagPalette.length)];
        Direction facing = Direction.from2DDataValue(random.nextInt(4));
        BlockState state = bag.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing);
        level.setBlock(pos, state, 2);
        return true;
    }

    private boolean isOpenFloor(WorldGenLevel level, BlockPos p) {
        return level.getBlockState(p).isAir()
                && level.getBlockState(p.above()).isAir()
                && isSturdyBelow(level, p);
    }

    private boolean isReplaceable(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(Blocks.WATER);
    }

    private boolean isSturdyBelow(WorldGenLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }
}
