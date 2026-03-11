package site.otools.Wasted.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.block.entity.ModBlockEntities;
import site.otools.Wasted.block.entity.RecyclerBlockEntity;

public class RecyclerBlock extends BaseEntityBlock {
    public static final MapCodec<RecyclerBlock> CODEC = simpleCodec(RecyclerBlock::new);

    public RecyclerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RecyclerBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving){
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof RecyclerBlockEntity recyclerBlockEntity){
                recyclerBlockEntity.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level plevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!plevel.isClientSide()) {
            BlockEntity entity = plevel.getBlockEntity(pPos);
            if(entity instanceof RecyclerBlockEntity recyclerBlockEntity){
                ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider(recyclerBlockEntity, Component.literal("Recycler")), pPos);
            } else {
                throw new IllegalStateException("Container provider is missing");
            }
        }

        return ItemInteractionResult.sidedSuccess(plevel.isClientSide());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()){
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.RECYCLER_BE.get(),
                (level1, blockPos, blockState, blockEntity) ->blockEntity.tick(level1, blockPos, blockState));
    }


}
