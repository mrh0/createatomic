package com.mrh0.createatomic.blocks.rod_assembly;

import com.mrh0.createatomic.index.AtomicItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum RodConfiguration implements StringRepresentable {
    None("none"),
    SmallControlRod("small_control_rod"),
    LargeControlRod("large_control_rod"),
    FuelRod("fuel_rod"),
    DepletedFuelRod("depleted_fuel_rod"),
    NeutronReflector("neutron_reflector");

    private String name;

    private RodConfiguration(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public MutableComponent getTooltip() {
        return switch (this) {
            case SmallControlRod -> Component.translatable("createatomic.tooltip.rod_assembly.small_control_rod");
            case LargeControlRod -> Component.translatable("createatomic.tooltip.rod_assembly.large_control_rod");
            case FuelRod -> Component.translatable("createatomic.tooltip.rod_assembly.fuel_rod");
            case DepletedFuelRod -> Component.translatable("createatomic.tooltip.rod_assembly.depleted_fuel_rod");
            case NeutronReflector -> Component.translatable("createatomic.tooltip.rod_assembly.neutron_reflector");
            default -> Component.translatable("createatomic.tooltip.rod_assembly.none");
        };
    }

    public ItemStack asStack() {
        return switch (this) {
            case SmallControlRod -> AtomicItems.SMALL_CONTROL_ROD.asStack();
            case LargeControlRod -> AtomicItems.LARGE_CONTROL_ROD.asStack();
            case FuelRod -> AtomicItems.FUEL_ROD.asStack();
            case DepletedFuelRod -> AtomicItems.DEPLETED_FUEL_ROD.asStack();
            case NeutronReflector -> AtomicItems.NEUTRON_REFLECTOR.asStack();
            default -> ItemStack.EMPTY;
        };
    }

    public static RodConfiguration fromStack(ItemStack stack) {
        Item item = stack.getItem();
        if(item == AtomicItems.SMALL_CONTROL_ROD.get()) return SmallControlRod;
        if(item == AtomicItems.LARGE_CONTROL_ROD.get()) return LargeControlRod;
        if(item == AtomicItems.FUEL_ROD.get()) return FuelRod;
        if(item == AtomicItems.DEPLETED_FUEL_ROD.get()) return DepletedFuelRod;
        if(item == AtomicItems.NEUTRON_REFLECTOR.get()) return NeutronReflector;
        return None;
    }

    public static boolean isAcceptedStack(ItemStack stack) {
        return fromStack(stack) != None;
    }

    public boolean isPopulated() {
        return this != None;
    }

    public int getControlLevel() {
        if(this == LargeControlRod) return 5;
        if(this == SmallControlRod) return 2;
        return 0;
    }

    public int getFuelLevel() {
        if(this == FuelRod) return 1;
        return 0;
    }

    public boolean isReflector() {
        return this == NeutronReflector;
    }

    public boolean isControlRod() {
        return this == SmallControlRod || this == LargeControlRod;
    }

    // True for any rod type that participates in the reaction and must be locked while running.
    public boolean isLockedWhileRunning() {
        return this == FuelRod || this == DepletedFuelRod || this == NeutronReflector;
    }
}
