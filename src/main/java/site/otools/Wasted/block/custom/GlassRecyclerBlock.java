package site.otools.Wasted.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import site.otools.Wasted.block.entity.GlassRecyclerBlockEntity;
import site.otools.Wasted.block.entity.ModBlockEntities;

import javax.annotation.Nullable;

public class GlassRecyclerBlock extends RecyclerBlock {
    public static final MapCodec<GlassRecyclerBlock> CODEC = simpleCodec(GlassRecyclerBlock::new);

    public GlassRecyclerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GlassRecyclerBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, ModBlockEntities.GLASS_RECYCLER_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
    }

    @Override
    protected MapCodec<? extends RecyclerBlock> codec() {
        return CODEC;
    }
}