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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import site.otools.Wasted.Config;
import site.otools.Wasted.block.ModBlocks;

public class TrashMountainFeature extends Feature<NoneFeatureConfiguration> {
    private static final int RADIUS = 10;
    private static final int MAX_HEIGHT = 7;
    private static final float TRASHBAG_TOP_CHANCE = 0.45f;


    private static final int SIZE_CHECK_DISTANCE = 16;
    private static final int[] SAMPLE_DX = {  0,  0,  SIZE_CHECK_DISTANCE, -SIZE_CHECK_DISTANCE,
                                              SIZE_CHECK_DISTANCE, -SIZE_CHECK_DISTANCE,
                                              SIZE_CHECK_DISTANCE, -SIZE_CHECK_DISTANCE };
    private static final int[] SAMPLE_DZ = { -SIZE_CHECK_DISTANCE,  SIZE_CHECK_DISTANCE,  0,  0,
                                             -SIZE_CHECK_DISTANCE, -SIZE_CHECK_DISTANCE,
                                              SIZE_CHECK_DISTANCE,  SIZE_CHECK_DISTANCE };

    public TrashMountainFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        float spawnChance = Config.TRASH_MOUNTAIN_SPAWN_CHANCE.get().floatValue();
        if (random.nextFloat() > spawnChance) return false;

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        int minSwampNeighbors = Config.TRASH_MOUNTAIN_MIN_SWAMP_NEIGHBORS.get();
        if (minSwampNeighbors > 0) {
            int qy = origin.getY() >> 2;
            int swampNeighbors = 0;
            for (int i = 0; i < SAMPLE_DX.length; i++) {
                int worldX = origin.getX() + SAMPLE_DX[i];
                int worldZ = origin.getZ() + SAMPLE_DZ[i];
                Holder<Biome> biome = level.getNoiseBiome(worldX >> 2, qy, worldZ >> 2);
                if (biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP)) {
                    swampNeighbors++;
                }
            }
            if (swampNeighbors < minSwampNeighbors) return false;
        }

        Block[] dirtPalette = {
                Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.PODZOL
        };
        Block[] bagPalette = {
                ModBlocks.TRASHBAG.get(), ModBlocks.TRASHBAG_V1.get(), ModBlocks.TRASHBAG_V2.get()
        };

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq > RADIUS * RADIUS) continue;

                double distRatio = Math.sqrt(distSq) / RADIUS;
                int columnHeight = (int) Math.round(MAX_HEIGHT * (1.0 - distRatio * distRatio));

                if (columnHeight > 1 && random.nextFloat() < 0.15f) columnHeight--;
                else if (random.nextFloat() < 0.1f) columnHeight++;

                if (columnHeight <= 0) continue;

                for (int dy = 0; dy < columnHeight; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    Block dirt = dirtPalette[random.nextInt(dirtPalette.length)];
                    level.setBlock(pos, dirt.defaultBlockState(), 2);
                }

                if (random.nextFloat() < TRASHBAG_TOP_CHANCE) {
                    BlockPos topPos = origin.offset(dx, columnHeight, dz);
                    Block bag = bagPalette[random.nextInt(bagPalette.length)];
                    Direction facing = Direction.from2DDataValue(random.nextInt(4));
                    BlockState bagState = bag.defaultBlockState()
                            .setValue(HorizontalDirectionalBlock.FACING, facing);
                    level.setBlock(topPos, bagState, 2);
                }
            }
        }
        return true;
    }
}
