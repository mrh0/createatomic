package github.mrh0.createatomic.blocks.turbine;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import github.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TurbineBlock extends DirectionalKineticBlock implements IWrenchable, IBE<TurbineBlockEntity> {

    public TurbineBlock(Properties props) {
        super(props);
    }

    // The shaft runs through the block on the FACING axis (both ends).
    // FACING = direction the shaft/power exits; FACING.opposite = reactor intake side.
    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }

    @Override
    public Class<TurbineBlockEntity> getBlockEntityClass() {
        return TurbineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TurbineBlockEntity> getBlockEntityType() {
        return AtomicBlockEntities.TURBINE.get();
    }

    @Override
    public net.minecraft.world.InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return net.minecraft.world.InteractionResult.SUCCESS;
    }
}
