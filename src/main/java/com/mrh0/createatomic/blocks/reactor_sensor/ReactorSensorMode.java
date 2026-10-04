package com.mrh0.createatomic.blocks.reactor_sensor;

import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;
import com.mrh0.createatomic.index.AtomicIcons;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public enum ReactorSensorMode implements INamedIconOptions {
    // Reactor heat is 25°C idle, 315°C at full hull load, and caps at 3× load (895°C).
    TEMPERATURE("temperature", AtomicIcons.I_TEMPERATURE, 25, 25 + 290 * 3, "°C"),
    HULL       ("hull",        AtomicIcons.I_HULL,        0,  100,          "%"),
    WATER      ("water",       AtomicIcons.I_WATER,       0,  100,          "%"),
    FUEL       ("fuel",        AtomicIcons.I_FUEL,        0,  100,          "%");

    private final String name;
    private final AllIcons icon;
    public final int min;
    public final int max;
    private final String unit;

    ReactorSensorMode(String name, AllIcons icon, int min, int max, String unit) {
        this.name = name;
        this.icon = icon;
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    // The reading in this mode's unit, clamped to [min, max]. Used by Threshold Switches and goggles.
    public int getValue(ReactorCasingBlockEntity reactor) {
        int value = switch (this) {
            case TEMPERATURE -> reactor.getTemperature();
            case HULL        -> Mth.ceil(reactor.getHealth());
            case WATER       -> Mth.ceil(reactor.getFillState() * 100);
            case FUEL        -> Mth.ceil(Math.max(0f, reactor.getLowestFuelRemaining()) * 100);
        };
        return Mth.clamp(value, min, max);
    }

    public int getComparatorOutput(ReactorCasingBlockEntity reactor) {
        return switch (this) {
            // Matches the reactor gauge: 0 when idle, 15 at 315°C and above.
            case TEMPERATURE -> ComparatorUtil.fractionToRedstoneLevel((reactor.getTemperature() - 25) / 290.0);
            case HULL        -> ComparatorUtil.fractionToRedstoneLevel(reactor.getHealth() / 100.0);
            case WATER       -> ComparatorUtil.fractionToRedstoneLevel(reactor.getFillState());
            // Same scale as the Rod Assembly: 0 = no fuel rods, 1 = a rod is depleted, 15 = all fresh.
            case FUEL -> {
                float fuel = reactor.getLowestFuelRemaining();
                yield fuel < 0 ? 0 : RodAssemblyBlockEntity.fuelFractionToRedstone(fuel);
            }
        };
    }

    public MutableComponent format(int value) {
        return Component.literal(value + unit);
    }

    @Override
    public AllIcons getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return "createatomic.reactor_sensor.mode." + name;
    }
}
