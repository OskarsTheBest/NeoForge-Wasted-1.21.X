package site.otools.Wasted.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import site.otools.Wasted.Config;
import site.otools.Wasted.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;

/**
 * A small surface "dump": a little dirt mound embedded into the ground with a few trashbags on top.
 * Several shape variants. Places on solid overworld land (never water), in any biome except swamps.
 */
public class TrashHeapFeature extends Feature<NoneFeatureConfiguration> {

    private static final int[][][] VARIANTS = {
            // 0: classic mini pyramid (3x3 base, raised centre)
            {{0,0,2},{1,0,1},{-1,0,1},{0,1,1},{0,-1,1},{1,1,1},{1,-1,1},{-1,1,1},{-1,-1,1}},
            // 1: flat 3x3 dirt patch
            {{0,0,1},{1,0,1},{-1,0,1},{0,1,1},{0,-1,1},{1,1,1},{1,-1,1},{-1,1,1},{-1,-1,1}},
            // 2: small 2x2 clump with one bump
            {{0,0,2},{1,0,1},{0,1,1},{1,1,1}},
            // 3: cross / plus with raised centre
            {{0,0,2},{1,0,1},{-1,0,1},{0,1,1},{0,-1,1}},
            // 4: irregular blob with two bumps
            {{0,0,2},{1,0,2},{-1,0,1},{0,1,1},{1,1,1},{-1,-1,1},{0,-1,1}}
    };

    public TrashHeapFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        if (random.nextFloat() > Config.TRASH_HEAP_SPAWN_CHANCE.get().floatValue()) return false;

        WorldGenLevel level = context.level();
        int x = context.origin().getX();
        int z = context.origin().getZ();

        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        int groundY = surfaceY - 1;
        BlockPos ground = new BlockPos(x, groundY, z);

        Holder<Biome> biome = level.getBiome(ground);
        if (biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP)) return false;

        BlockState surfaceState = level.getBlockState(ground);
        if (!surfaceState.getFluidState().isEmpty()) return false;
        if (!surfaceState.isFaceSturdy(level, ground, Direction.UP)) return false;

        Block[] dirtPalette = {
                Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.PODZOL
        };
        Block[] bagPalette = {
                ModBlocks.TRASHBAG.get(), ModBlocks.TRASHBAG_V1.get(), ModBlocks.TRASHBAG_V2.get()
        };

        int[][] variant = VARIANTS[random.nextInt(VARIANTS.length)];


        List<BlockPos> bagSpots = new ArrayList<>();
        for (int[] cell : variant) {
            int cx = x + cell[0];
            int cz = z + cell[1];
            int height = cell[2];
            for (int i = 0; i < height; i++) {
                BlockPos dirtPos = new BlockPos(cx, groundY + i, cz);
                level.setBlock(dirtPos, dirtPalette[random.nextInt(dirtPalette.length)].defaultBlockState(), 2);
            }
            bagSpots.add(new BlockPos(cx, groundY + height, cz));
        }


        for (int i = bagSpots.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockPos tmp = bagSpots.get(i);
            bagSpots.set(i, bagSpots.get(j));
            bagSpots.set(j, tmp);
        }

        int wanted = 3 + random.nextInt(3);
        int placed = 0;
        for (BlockPos spot : bagSpots) {
            if (placed >= wanted) break;
            if (!isClear(level, spot)) continue;
            Block bag = bagPalette[random.nextInt(bagPalette.length)];
            Direction facing = Direction.from2DDataValue(random.nextInt(4));
            level.setBlock(spot, bag.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing), 2);
            placed++;
        }
        return placed > 0;
    }


    private boolean isClear(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.getFluidState().isEmpty()) return false;
        return state.isAir() || state.canBeReplaced();
    }
}
