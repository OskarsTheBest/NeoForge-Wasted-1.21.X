package site.otools.Wasted.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.item.ModItems;

public class TrashbagBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<TrashbagBlock> CODEC = simpleCodec(TrashbagBlock::new);

    public TrashbagBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }



    @Override
    protected float getShadeBrightness(BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos) {
        return 1.0F;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player.getMainHandItem().is(ModItems.TRASHGRABBER.get())) {
            return 1.0F;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos,
                              BlockState state, @Nullable BlockEntity blockEntity,
                              ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (level.isClientSide) return;

        boolean withGrabber = tool.is(ModItems.TRASHGRABBER.get());
        int trashbagChance = withGrabber ? 50 : 25;

        ItemStack drop;
        if (level.random.nextInt(100) < trashbagChance) {
            drop = new ItemStack(ModItems.TRASH.get());
        } else {
            drop = switch (level.random.nextInt(3)) {
                case 0 -> new ItemStack(ModItems.GLASSHATTER.get());
                case 1 -> new ItemStack(ModItems.METAL.get());
                default -> new ItemStack(ModItems.PLASTIC.get());
            };
        }
        popResource(level, pos, drop);
    }
}