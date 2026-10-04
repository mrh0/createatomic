package com.mrh0.createatomic.blocks.reactor_sensor;

import com.mrh0.createatomic.blocks.reactor_casing.ReactorAttachmentBlock;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// Reads one value from the attached reactor and exposes it as a comparator signal and to Create's Threshold Switch.
public class ReactorSensorBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, ThresholdSwitchObservable {

    private ScrollOptionBehaviour<ReactorSensorMode> mode;
    private int lastComparatorOutput = -1;

    public ReactorSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        mode = new ScrollOptionBehaviour<>(ReactorSensorMode.class,
                Component.translatable("createatomic.reactor_sensor.mode"), this,
                new CenteredSideValueBoxTransform((state, dir) -> dir != state.getValue(ReactorAttachmentBlock.FACING)));
        mode.withCallback($ -> notifyComparatorIfChanged());
        behaviours.add(mode);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        notifyComparatorIfChanged();
    }

    public ReactorSensorMode getMode() {
        return mode.get();
    }

    @Nullable
    public ReactorCasingBlockEntity findReactor() {
        if (level == null) return null;
        return ReactorAttachmentBlock.findReactor(level, worldPosition, getBlockState());
    }

    public int getComparatorOutput() {
        ReactorCasingBlockEntity reactor = findReactor();
        return reactor == null ? 0 : getMode().getComparatorOutput(reactor);
    }

    private void notifyComparatorIfChanged() {
        if (level == null || level.isClientSide()) return;
        int output = getComparatorOutput();
        if (output == lastComparatorOutput) return;
        lastComparatorOutput = output;
        level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }

    @Override
    public int getMinValue() {
        return getMode().min;
    }

    @Override
    public int getMaxValue() {
        return getMode().max;
    }

    @Override
    public int getCurrentValue() {
        ReactorCasingBlockEntity reactor = findReactor();
        return reactor == null ? getMode().min : getMode().getValue(reactor);
    }

    @Override
    public MutableComponent format(int value) {
        return getMode().format(value);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        String spacing = "    ";
        tooltip.add(Component.literal(spacing).append(
                Component.translatable("block.createatomic.reactor_sensor").withStyle(ChatFormatting.WHITE)));

        ReactorCasingBlockEntity reactor = findReactor();
        if (reactor == null) {
            tooltip.add(Component.literal(spacing + " ").append(
                    Component.translatable("createatomic.tooltip.reactor_sensor.no_reactor").withStyle(ChatFormatting.RED)));
            return true;
        }

        ReactorSensorMode mode = getMode();
        tooltip.add(Component.literal(spacing + " ").append(
                Component.translatable("createatomic.tooltip.reactor_sensor.mode").withStyle(ChatFormatting.GRAY)
                        .append(Component.translatable(mode.getTranslationKey()).withStyle(ChatFormatting.AQUA))));
        tooltip.add(Component.literal(spacing + " ").append(
                Component.translatable("createatomic.tooltip.reactor_sensor.value").withStyle(ChatFormatting.GRAY)
                        .append(mode.format(mode.getValue(reactor)).withStyle(ChatFormatting.AQUA))));
        tooltip.add(Component.literal(spacing + " ").append(
                Component.translatable("createatomic.tooltip.reactor_sensor.signal").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(String.valueOf(mode.getComparatorOutput(reactor))).withStyle(ChatFormatting.RED))));
        return true;
    }
}
