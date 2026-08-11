package com.mrh0.createatomic.blocks.rtg;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.mrh0.createatomic.Utility;
import com.mrh0.createatomic.config.AtomicConfigs;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.createmod.catnip.lang.Lang;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class RTGBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private int storedEnergy = 0;

    private final IEnergyStorage energyCapability = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0; // RTG only outputs; external blocks cannot push energy in
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int amount = Math.min(maxExtract, storedEnergy);
            if (!simulate) {
                storedEnergy -= amount;
                setChanged();
            }
            return amount;
        }

        @Override
        public int getEnergyStored() {
            return storedEnergy;
        }

        @Override
        public int getMaxEnergyStored() {
            return AtomicConfigs.server().rtgBufferCapacity.get();
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    public RTGBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) return;

        int perTick = AtomicConfigs.server().rtgEnergyPerTick.get();
        int capacity = AtomicConfigs.server().rtgBufferCapacity.get();

        int toGenerate = Math.min(perTick, capacity - storedEnergy);
        if (toGenerate > 0) {
            storedEnergy += toGenerate;
            setChanged();
        }

        pushEnergyToNeighbors();
    }

    private void pushEnergyToNeighbors() {
        if (level == null || storedEnergy <= 0) return;

        for (Direction dir : Direction.values()) {
            if (storedEnergy <= 0) break;
            BlockPos neighborPos = getBlockPos().relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;
            IEnergyStorage neighbor = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighbor == null || !neighbor.canReceive()) continue;
            int sent = neighbor.receiveEnergy(storedEnergy, false);
            storedEnergy -= sent;
            if (sent > 0) setChanged();
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AtomicBlockEntities.RTG.get(),
                (be, context) -> be.energyCapability
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        int perTick = AtomicConfigs.server().rtgEnergyPerTick.get();

        Lang.builder("createatomic")
                .add(Component.translatable("createatomic.tooltip.rtg.generation").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        Lang.builder("createatomic")
                .add(Utility.getTextComponent(perTick, "⚡/t").withStyle(ChatFormatting.GREEN))
                .forGoggles(tooltip, 1);

        Lang.builder("createatomic")
                .add(Component.translatable("createatomic.tooltip.rtg.stored").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        Lang.builder("createatomic")
                .add(Utility.getTextComponent(energyCapability))
                .forGoggles(tooltip, 1);

        return true;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("Energy", storedEnergy);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        storedEnergy = tag.getInt("Energy");
    }
}
