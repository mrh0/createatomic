package com.mrh0.createatomic.blocks.rtg;

import com.mrh0.createatomic.Utility;
import com.mrh0.createatomic.config.AtomicConfigs;
import com.mrh0.createatomic.index.AtomicBlocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class RTGBlockItem extends BlockItem {

    public RTGBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("Energy");
    }

    public static void setEnergy(ItemStack stack, int energy) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, cd -> {
            CompoundTag tag = cd.copyTag();
            tag.putInt("Energy", energy);
            return CustomData.of(tag);
        });
    }

    private static IEnergyStorage createEnergyStorage(ItemStack stack) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int current = getEnergy(stack);
                int capacity = AtomicConfigs.server().rtgBufferCapacity.get();
                int toReceive = Math.min(maxReceive, capacity - current);
                if (!simulate && toReceive > 0) setEnergy(stack, current + toReceive);
                return toReceive;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int current = getEnergy(stack);
                int toExtract = Math.min(maxExtract, current);
                if (!simulate && toExtract > 0) setEnergy(stack, current - toExtract);
                return toExtract;
            }

            @Override public int getEnergyStored() { return getEnergy(stack); }
            @Override public int getMaxEnergyStored() { return AtomicConfigs.server().rtgBufferCapacity.get(); }
            @Override public boolean canExtract() { return true; }
            @Override public boolean canReceive() { return true; }
        };
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide()) return;
        int perTick = AtomicConfigs.server().rtgEnergyPerTick.get();
        int capacity = AtomicConfigs.server().rtgBufferCapacity.get();
        int current = getEnergy(stack);
        if (current < capacity) setEnergy(stack, Math.min(current + perTick, capacity));
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> createEnergyStorage(stack),
                AtomicBlocks.RTG.asItem()
        );
    }

    /* 
    @Override public boolean isBarVisible(ItemStack stack) { return true; }

    @Override
    public int getBarWidth(ItemStack stack) {
        int capacity = AtomicConfigs.server().rtgBufferCapacity.get();
        if (capacity <= 0) return 0;
        return Math.round(13f * getEnergy(stack) / capacity);
    }

    @Override public int getBarColor(ItemStack stack) { return 0x00BFFF; }
    

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Utility.getTextComponent(createEnergyStorage(stack)));
    }
    */
}
