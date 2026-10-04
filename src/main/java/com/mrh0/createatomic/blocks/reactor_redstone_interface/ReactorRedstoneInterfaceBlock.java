package com.mrh0.createatomic.blocks.reactor_redstone_interface;

import com.mrh0.createatomic.blocks.reactor_casing.ReactorAttachmentBlock;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ReactorRedstoneInterfaceBlock extends ReactorAttachmentBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ReactorRedstoneInterfaceBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.DOWN)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return state.setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide() || state.getBlock() == oldState.getBlock()) return;
        notifyReactor(level, pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && state.getBlock() != newState.getBlock())
            notifyReactor(level, pos, state);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide()) return;

        boolean nowPowered = level.hasNeighborSignal(pos);
        boolean wasPowered = state.getValue(POWERED);
        if (nowPowered == wasPowered) return;

        level.setBlock(pos, state.setValue(POWERED, nowPowered), Block.UPDATE_CLIENTS);
        notifyReactor(level, pos, state.setValue(POWERED, nowPowered));
    }

    private static void notifyReactor(Level level, BlockPos pos, BlockState state) {
        ReactorCasingBlockEntity controller = findReactor(level, pos, state);
        if (controller != null)
            controller.rescanInterfaces();
    }
}
