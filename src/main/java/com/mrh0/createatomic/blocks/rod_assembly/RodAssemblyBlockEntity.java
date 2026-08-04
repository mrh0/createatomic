package com.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.mrh0.createatomic.config.AtomicConfigs;
import com.mrh0.createatomic.network.IObserveBlockEntity;
import com.mrh0.createatomic.network.ObservePacketPayload;
import com.mrh0.createatomic.network.RodAssemblyPacketPayload;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RodAssemblyBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IObserveBlockEntity {

    private static int fuelDuration() { return AtomicConfigs.server().fuelRodDuration.get(); }

    private ItemStack currentRod = ItemStack.EMPTY;
    private int fuelTicks = 0;

    // Client-only: smoothly animates the rod 4px down when the reactor is running.
    public LerpedFloat insertAnimation;

    public RodAssemblyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {}

    @Override
    public void tick() {
        super.tick();
        if (level == null || !level.isClientSide()) return;
        if (insertAnimation == null)
            insertAnimation = LerpedFloat.linear().startWithValue(0f);
        insertAnimation.tickChaser();
        ReactorCasingBlockEntity reactor = findReactor();
        RodConfiguration config = getConfig();
        boolean shouldInsert = reactor != null && config.shouldInsert(reactor.isActive(), reactor.isArmed());
        insertAnimation.chase(shouldInsert ? 1f : 0f, 0.15f, Chaser.EXP);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!currentRod.isEmpty())
            tag.put("rod", currentRod.save(registries));
        tag.putInt("fuelTicks", fuelTicks);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        currentRod = ItemStack.EMPTY;
        if (tag.contains("rod"))
            currentRod = ItemStack.parse(registries, tag.getCompound("rod")).orElse(ItemStack.EMPTY);
        fuelTicks = tag.getInt("fuelTicks");
        if (clientPacket) RodAssemblyPacketPayload.clientFuelTicks = fuelTicks;
    }

    public RodConfiguration getConfig() {
        return RodConfiguration.fromStack(currentRod);
    }

    @Nullable
    public ReactorCasingBlockEntity findReactor() {
        if (level == null) return null;
        BlockEntity below = level.getBlockEntity(worldPosition.below());
        if (!(below instanceof ReactorCasingBlockEntity rce)) return null;
        return rce.getControllerBE();
    }

    public boolean isReactorActive() {
        ReactorCasingBlockEntity reactor = findReactor();
        return reactor != null && reactor.isActive();
    }

    public boolean isLocked() {
        return isLockedWith(getConfig());
    }

    public boolean isLockedWith(RodConfiguration config) {
        ReactorCasingBlockEntity reactor = findReactor();
        if (reactor == null) return false;
        return config.shouldInsert(reactor.isActive(), reactor.isArmed());
    }

    public ItemStack getCurrentRod() {
        return currentRod.copy();
    }

    public void updateRod(ItemStack stack) {
        currentRod = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);

        // Restore depletion progress that was embedded on the item when it was last extracted.
        fuelTicks = 0;
        if (!currentRod.isEmpty()) {
            CustomData custom = currentRod.get(DataComponents.CUSTOM_DATA);
            if (custom != null) fuelTicks = custom.copyTag().getInt("FuelTicks");
        }

        if (hasLevel() && !level.isClientSide()) {
            setChanged();
            sendData();
        }
    }

    // Returns the rod item with fuelTicks embedded so that re-inserting it later resumes depletion.
    public ItemStack getRodWithDepletion() {
        RodConfiguration config = getConfig();
        ItemStack rod = config.asStack();
        if (rod.isEmpty()) return ItemStack.EMPTY;
        if (config.isFuelRod() && fuelTicks > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("FuelTicks", fuelTicks);
            rod.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return rod;
    }

    // Called every lazy tick by the reactor controller to advance fuel consumption.
    // gameTicks is the reactor's lazyTickRate so fuelTicks counts real game ticks,
    // matching the config unit (fuelRodDuration is in game ticks).
    // consumptionBonus is the sum of adjacentFuelConsumptionBonus from all neighbours
    // (e.g. 0.2 = 20% faster depletion).
    public void tickRod(int gameTicks, float consumptionBonus) {
        if (level == null || level.isClientSide()) return;
        RodConfiguration config = getConfig();
        if (!config.isFuelRod()) return;

        fuelTicks += Math.round(gameTicks * (1f + consumptionBonus));
        if (fuelTicks >= fuelDuration()) {
            fuelTicks = 0;
            updateRod(config.depleteInto().asStack());
        }
    }

    private float computeAdjacentConsumptionBonus() {
        if (level == null) return 0f;
        int[] dx = {-1, 1, 0, 0};
        int[] dz = { 0, 0,-1, 1};
        float bonus = 0f;
        for (int d = 0; d < 4; d++) {
            net.minecraft.world.level.block.entity.BlockEntity neighbor =
                    level.getBlockEntity(worldPosition.offset(dx[d], 0, dz[d]));
            if (neighbor instanceof RodAssemblyBlockEntity neighbourRod)
                bonus += neighbourRod.getConfig().adjacentFuelConsumptionBonus;
        }
        return bonus;
    }

    @Override
    public void onObserved(ServerPlayer player, ObservePacketPayload pack) {
        RodAssemblyPacketPayload.send(fuelTicks, player);
    }

    public int getControlLevel() {
        return getConfig().getControlLevel();
    }

    public int getFuelLevel() {
        return getConfig().getFuelLevel();
    }

    public int getFuelProgress() {
        return fuelTicks;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ObservePacketPayload.send(worldPosition, 0);
        RodConfiguration config = getConfig();
        String spacing = "    ";
        tooltip.add(Component.literal(spacing).append(
                Component.translatable("block.createatomic.rod_assembly").withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal(spacing + " ").append(config.getTooltip().withStyle(ChatFormatting.GRAY)));
        if (config.isFuelRod()) {
            int duration = fuelDuration();
            int remainingSeconds = Math.max(0, duration - RodAssemblyPacketPayload.clientFuelTicks) / 20;
            int hours = remainingSeconds / 3600;
            int minutes = (remainingSeconds % 3600) / 60;
            int seconds = remainingSeconds % 60;
            int pct = (RodAssemblyPacketPayload.clientFuelTicks * 100) / Math.max(1, duration);
            ChatFormatting color = pct == 0 ? ChatFormatting.GREEN : pct < 75 ? ChatFormatting.YELLOW : ChatFormatting.RED;
            tooltip.add(Component.literal(spacing + " ").append(
                    Component.translatable("createatomic.tooltip.fuel_rod.depletion",
                            String.format("%02dh:%02dm:%02ds", hours, minutes, seconds)).withStyle(color)));
            float consumptionBonus = computeAdjacentConsumptionBonus();
            if (consumptionBonus > 0f) {
                tooltip.add(Component.literal(spacing + " ").append(
                        Component.translatable("createatomic.tooltip.fuel_rod.consumption_bonus",
                                String.format("%.0f%%", consumptionBonus * 100)).withStyle(ChatFormatting.GOLD)));
            }
        }
        if (isLocked()) {
            tooltip.add(Component.literal(spacing + " ").append(
                    Component.translatable("createatomic.tooltip.rod_assembly.locked").withStyle(ChatFormatting.RED)));
        }
        return true;
    }
}
