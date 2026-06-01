package site.otools.Wasted.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.block.entity.ModBlockEntities;
import site.otools.Wasted.block.entity.TrashGeneratorBlockEntity;


public class TrashGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<TrashGeneratorBlock> CODEC = simpleCodec(TrashGeneratorBlock::new);

    public TrashGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrashGeneratorBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TrashGeneratorBlockEntity gen) {
                gen.drops();
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> beType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(beType, ModBlockEntities.TRASH_GENERATOR_BE.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }
}
