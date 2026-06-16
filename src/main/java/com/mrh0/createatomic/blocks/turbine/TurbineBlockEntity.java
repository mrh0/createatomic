package com.mrh0.createatomic.blocks.turbine;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TurbineBlockEntity extends GeneratingKineticBlockEntity {

    private float generatedRpm = 0f;

    public TurbineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public float getGeneratedSpeed() {
        return generatedRpm;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return (float) BlockStressValues.getCapacity(getBlockState().getBlock());
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level == null || level.isClientSide()) return;
        refreshGeneratedSpeed();
    }

    // Walks back through any chain of same-facing turbines to find the reactor controller.
    // Returns null if the chain terminates without hitting a reactor (or exceeds 16 blocks).
    private ReactorCasingBlockEntity findConnectedReactor() {
        if (level == null) return null;
        Direction facing     = getBlockState().getValue(TurbineBlock.FACING);
        Direction intakeSide = facing.getOpposite();
        BlockPos pos = getBlockPos().relative(intakeSide);
        for (int depth = 0; depth < 16; depth++) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ReactorCasingBlockEntity reactorBE)
                return reactorBE.getControllerBE();
            if (be instanceof TurbineBlockEntity upstream
                    && upstream.getBlockState().getValue(TurbineBlock.FACING) == facing) {
                pos = pos.relative(intakeSide);
                continue;
            }
            break;
        }
        return null;
    }

    private void refreshGeneratedSpeed() {
        ReactorCasingBlockEntity ctrl = findConnectedReactor();
        applyGeneratedRpm(ctrl != null ? ctrl.turbineTargetRpm : 0f);
    }

    private void applyGeneratedRpm(float newRpm) {
        if (Math.abs(newRpm - generatedRpm) < 0.5f) return;
        generatedRpm = newRpm;
        updateGeneratedRotation();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        String s = "  ";
        ReactorCasingBlockEntity reactor = findConnectedReactor();

        if (reactor == null) {
            tooltip.add(Component.literal(s).append(
                    Component.translatable("createatomic.tooltip.turbine.no_reactor").withStyle(ChatFormatting.DARK_RED)));
        } else if (generatedRpm < 0.5f) {
            tooltip.add(Component.literal(s).append(
                    Component.translatable("createatomic.tooltip.turbine.no_output").withStyle(ChatFormatting.DARK_GRAY)));
        } else {
            tooltip.add(Component.literal(s).append(
                    Component.translatable("createatomic.tooltip.turbine.rpm", String.format("%.0f", generatedRpm)).withStyle(ChatFormatting.GREEN)));
        }
        return true;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("TurbineRpm", generatedRpm);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        generatedRpm = tag.getFloat("TurbineRpm");
    }
}
