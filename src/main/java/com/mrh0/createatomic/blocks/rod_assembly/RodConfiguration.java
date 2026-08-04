package com.mrh0.createatomic.blocks.rod_assembly;

import com.mrh0.createatomic.index.AtomicItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum RodConfiguration implements StringRepresentable {
    None                   ("none",                   0, 0,  0f,   0f,   RodInsertionBehaviour.NEVER),
    SmallControlRod        ("small_control_rod",       0, 2,  0f,   0f,   RodInsertionBehaviour.SCRAM),
    LargeControlRod        ("large_control_rod",       0, 5,  0f,   0f,   RodInsertionBehaviour.SCRAM),
    FuelRod                ("fuel_rod",                1, 0,  0.5f, 0f,   RodInsertionBehaviour.ACTIVE),
    DepletedFuelRod        ("depleted_fuel_rod",       0, 0,  0f,   0f,   RodInsertionBehaviour.NEVER),
    PlutoniumFuelRod       ("plutonium_fuel_rod",      1, -2, 0.5f, 0.2f, RodInsertionBehaviour.ACTIVE),
    DepletedPlutoniumFuelRod("depleted_plutonium_fuel_rod", 0, 0, 0f, 0f, RodInsertionBehaviour.NEVER),
    NeutronReflector       ("neutron_reflector",       0, 0,  0.5f, 0.1f, RodInsertionBehaviour.ARMED);

    private final String name;
    public final int effectivePower;
    public final int hullCapacity;
    public final float adjacencyBonus;
    public final float adjacentFuelConsumptionBonus;
    private final RodInsertionBehaviour insertionBehaviour;

    RodConfiguration(String name, int effectivePower, int hullCapacity, float adjacencyBonus, float adjacentFuelConsumptionBonus, RodInsertionBehaviour insertionBehaviour) {
        this.name = name;
        this.effectivePower = effectivePower;
        this.hullCapacity = hullCapacity;
        this.adjacencyBonus = adjacencyBonus;
        this.adjacentFuelConsumptionBonus = adjacentFuelConsumptionBonus;
        this.insertionBehaviour = insertionBehaviour;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public MutableComponent getTooltip() {
        return switch (this) {
            case SmallControlRod         -> Component.translatable("createatomic.tooltip.rod_assembly.small_control_rod");
            case LargeControlRod         -> Component.translatable("createatomic.tooltip.rod_assembly.large_control_rod");
            case FuelRod                 -> Component.translatable("createatomic.tooltip.rod_assembly.fuel_rod");
            case DepletedFuelRod         -> Component.translatable("createatomic.tooltip.rod_assembly.depleted_fuel_rod");
            case PlutoniumFuelRod        -> Component.translatable("createatomic.tooltip.rod_assembly.plutonium_fuel_rod");
            case DepletedPlutoniumFuelRod -> Component.translatable("createatomic.tooltip.rod_assembly.depleted_plutonium_fuel_rod");
            case NeutronReflector        -> Component.translatable("createatomic.tooltip.rod_assembly.neutron_reflector");
            default -> Component.translatable("createatomic.tooltip.rod_assembly.none");
        };
    }

    public ItemStack asStack() {
        return switch (this) {
            case SmallControlRod         -> AtomicItems.SMALL_CONTROL_ROD.asStack();
            case LargeControlRod         -> AtomicItems.LARGE_CONTROL_ROD.asStack();
            case FuelRod                 -> AtomicItems.FUEL_ROD.asStack();
            case DepletedFuelRod         -> AtomicItems.DEPLETED_FUEL_ROD.asStack();
            case PlutoniumFuelRod        -> AtomicItems.PLUTONIUM_FUEL_ROD.asStack();
            case DepletedPlutoniumFuelRod -> AtomicItems.DEPLETED_PLUTONIUM_FUEL_ROD.asStack();
            case NeutronReflector        -> AtomicItems.NEUTRON_REFLECTOR.asStack();
            default -> ItemStack.EMPTY;
        };
    }

    public static RodConfiguration fromStack(ItemStack stack) {
        Item item = stack.getItem();
        if (item == AtomicItems.SMALL_CONTROL_ROD.get())          return SmallControlRod;
        if (item == AtomicItems.LARGE_CONTROL_ROD.get())          return LargeControlRod;
        if (item == AtomicItems.FUEL_ROD.get())                   return FuelRod;
        if (item == AtomicItems.DEPLETED_FUEL_ROD.get())          return DepletedFuelRod;
        if (item == AtomicItems.PLUTONIUM_FUEL_ROD.get())         return PlutoniumFuelRod;
        if (item == AtomicItems.DEPLETED_PLUTONIUM_FUEL_ROD.get()) return DepletedPlutoniumFuelRod;
        if (item == AtomicItems.NEUTRON_REFLECTOR.get())          return NeutronReflector;
        return None;
    }

    public boolean isFuelRod() {
        return this == FuelRod || this == PlutoniumFuelRod;
    }

    public RodConfiguration depleteInto() {
        return switch (this) {
            case FuelRod         -> DepletedFuelRod;
            case PlutoniumFuelRod -> DepletedPlutoniumFuelRod;
            default -> None;
        };
    }

    public static boolean isAcceptedStack(ItemStack stack) {
        return fromStack(stack) != None;
    }

    public boolean isPopulated() {
        return this != None;
    }

    public int getControlLevel() {
        return hullCapacity;
    }

    public int getFuelLevel() {
        return effectivePower;
    }

    public boolean shouldInsert(boolean active, boolean armed) {
        return RodInsertionBehaviour.shouldInsert(insertionBehaviour, active, armed);
    }
}
