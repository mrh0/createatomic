package com.mrh0.createatomic.blocks.reactor_sensor;

import com.mrh0.createatomic.blocks.reactor_casing.ReactorAttachmentBlock;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorSensorBlock extends ReactorAttachmentBlock implements IBE<ReactorSensorBlockEntity> {

    public ReactorSensorBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return getBlockEntityOptional(level, pos).map(ReactorSensorBlockEntity::getComparatorOutput).orElse(0);
    }

    @Override
    public Class<ReactorSensorBlockEntity> getBlockEntityClass() {
        return ReactorSensorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ReactorSensorBlockEntity> getBlockEntityType() {
        return AtomicBlockEntities.REACTOR_SENSOR.get();
    }
}
