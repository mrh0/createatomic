package com.mrh0.createatomic.blocks.rtg;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RTGBlock extends Block implements IBE<RTGBlockEntity>, IWrenchable {

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 10, 16);

    public RTGBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public Class<RTGBlockEntity> getBlockEntityClass() {
        return RTGBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RTGBlockEntity> getBlockEntityType() {
        return AtomicBlockEntities.RTG.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }
}
