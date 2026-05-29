package com.mrh0.createatomic.index;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.display.ReactorHealthDisplaySource;
import com.mrh0.createatomic.display.ReactorStatusDisplaySource;
import com.mrh0.createatomic.display.ReactorTemperatureDisplaySource;
import com.mrh0.createatomic.display.ReactorWaterDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AtomicDisplaySources {

    public static RegistryEntry<DisplaySource, ReactorStatusDisplaySource> REACTOR_STATUS;
    public static RegistryEntry<DisplaySource, ReactorTemperatureDisplaySource> REACTOR_TEMPERATURE;
    public static RegistryEntry<DisplaySource, ReactorHealthDisplaySource> REACTOR_HEALTH;
    public static RegistryEntry<DisplaySource, ReactorWaterDisplaySource> REACTOR_WATER;

    public static void register() {
        REACTOR_STATUS = CreateAtomic.REGISTRATE
                .displaySource("reactor_status", ReactorStatusDisplaySource::new)
                .register();
        REACTOR_TEMPERATURE = CreateAtomic.REGISTRATE
                .displaySource("reactor_temperature", ReactorTemperatureDisplaySource::new)
                .register();
        REACTOR_HEALTH = CreateAtomic.REGISTRATE
                .displaySource("reactor_health", ReactorHealthDisplaySource::new)
                .register();
        REACTOR_WATER = CreateAtomic.REGISTRATE
                .displaySource("reactor_water", ReactorWaterDisplaySource::new)
                .register();
    }

    public static void associateWithBlockEntities() {
        BlockEntityType<?> reactorType = AtomicBlockEntities.REACTOR_CASING.get();
        DisplaySource.BY_BLOCK_ENTITY.add(reactorType, REACTOR_STATUS.get());
        DisplaySource.BY_BLOCK_ENTITY.add(reactorType, REACTOR_TEMPERATURE.get());
        DisplaySource.BY_BLOCK_ENTITY.add(reactorType, REACTOR_HEALTH.get());
        DisplaySource.BY_BLOCK_ENTITY.add(reactorType, REACTOR_WATER.get());
    }
}
