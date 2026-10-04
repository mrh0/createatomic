package com.mrh0.createatomic.blocks.reactor_casing;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

// A full block placed against one face of a Reactor Casing multi-block. FACING points toward the casing.
public abstract class ReactorAttachmentBlock extends Block implements IWrenchable {

    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

    protected ReactorAttachmentBlock(Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos attachPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        if (!(context.getLevel().getBlockState(attachPos).getBlock() instanceof ReactorCasingBlock))
            return null;
        return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos attachPos = pos.relative(state.getValue(FACING));
        return level.getBlockState(attachPos).getBlock() instanceof ReactorCasingBlock;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Nullable
    public static ReactorCasingBlockEntity findReactor(BlockGetter level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos.relative(state.getValue(FACING)));
        if (!(be instanceof ReactorCasingBlockEntity rce)) return null;
        return rce.getControllerBE();
    }
}
