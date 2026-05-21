package github.mrh0.createatomic.blocks.reactor_redstone_interface;

import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class ReactorRedstoneInterfaceBlock extends Block {

    public static final EnumProperty<Direction> FACING  = DirectionalBlock.FACING;
    public static final BooleanProperty         POWERED = BlockStateProperties.POWERED;

    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
        Direction.DOWN,  Block.box( 3,  0,  3, 13,  4, 13),
        Direction.UP,    Block.box( 3, 12,  3, 13, 16, 13),
        Direction.NORTH, Block.box( 3,  3,  0, 13, 13,  4),
        Direction.SOUTH, Block.box( 3,  3, 12, 13, 13, 16),
        Direction.EAST,  Block.box(12,  3,  3, 16, 13, 13),
        Direction.WEST,  Block.box( 0,  3,  3,  4, 13, 13)
    );

    public ReactorRedstoneInterfaceBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.DOWN)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return defaultBlockState()
                .setValue(FACING, context.getClickedFace().getOpposite())
                .setValue(POWERED, powered);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide()) return;

        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED))
            level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_ALL);

        Direction facing = state.getValue(FACING);
        BlockPos reactorPos = pos.relative(facing);
        BlockEntity be = level.getBlockEntity(reactorPos);
        if (be instanceof ReactorCasingBlockEntity reactor)
            reactor.onRedstoneInterfaceChanged();
    }
}
