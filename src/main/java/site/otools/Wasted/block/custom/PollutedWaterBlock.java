package site.otools.Wasted.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import site.otools.Wasted.fluid.ModFluids;
import site.otools.Wasted.pollution.PollutionLogic;

/** Custom water-like fluid block: damages swimmers (Hunger + Nausea) */
public class PollutedWaterBlock extends LiquidBlock {

    public PollutedWaterBlock(BlockBehaviour.Properties properties) {
        super((FlowingFluid) ModFluids.POLLUTED_WATER.get(), properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, true, false));
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0, true, false));
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        PollutionLogic.spreadStep(level, pos, random);
    }
}
