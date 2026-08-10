package com.mrh0.createatomic.index;

import com.mrh0.createatomic.CreateAtomic;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AtomicParticleTypes {

    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, CreateAtomic.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MUSHROOM_CLOUD =
            PARTICLE_TYPES.register("mushroom_cloud", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
