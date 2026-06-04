package site.otools.Wasted.pollution;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import site.otools.Wasted.Config;
import site.otools.Wasted.block.ModBlocks;
import site.otools.Wasted.block.custom.TrashbagBlock;
import site.otools.Wasted.item.ModItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Water pollution: trash/trashbag items thrown into water seed one polluted block, which then spreads
 * to adjacent water sources
 */
public final class PollutionLogic {
    private PollutionLogic() {}

    private static boolean isVanillaWaterSource(BlockState state) {
        return state.is(Blocks.WATER) && state.getFluidState().isSource();
    }

    private static BlockState pollutedState() {
        return ModBlocks.POLLUTED_WATER_BLOCK.get().defaultBlockState();
    }

    private static Block pollutedBlock() {
        return ModBlocks.POLLUTED_WATER_BLOCK.get();
    }

    public static boolean isPollutingItemStack(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(ModItems.TRASH.get())) return true;
        return stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof TrashbagBlock;
    }

    public static boolean seedPollution(ServerLevel level, BlockPos pos) {
        if (!Config.POLLUTION_ENABLED.get()) return false;
        BlockPos[] candidates = {
                pos, pos.below(), pos.above(),
                pos.north(), pos.south(), pos.east(), pos.west()
        };
        for (BlockPos c : candidates) {
            if (isVanillaWaterSource(level.getBlockState(c))) {
                level.setBlock(c, pollutedState(), 3);
                level.scheduleTick(c, pollutedBlock(), Config.POLLUTION_SPREAD_DELAY.get());
                return true;
            }
        }
        return false;
    }

    public static void spreadStep(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!Config.POLLUTION_ENABLED.get()) return;

        List<BlockPos> water = new ArrayList<>();
        for (Direction d : Direction.values()) {
            BlockPos n = pos.relative(d);
            if (isVanillaWaterSource(level.getBlockState(n))) {
                water.add(n);
            }
        }
        if (water.isEmpty()) return;

        int delay = Config.POLLUTION_SPREAD_DELAY.get();
        BlockPos target = water.get(random.nextInt(water.size()));
        level.setBlock(target, pollutedState(), 3);
        level.scheduleTick(target, pollutedBlock(), delay);

        if (water.size() > 1) {
            level.scheduleTick(pos, pollutedBlock(), delay);
        }
    }
}
