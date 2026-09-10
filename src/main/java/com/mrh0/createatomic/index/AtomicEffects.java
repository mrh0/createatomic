package com.mrh0.createatomic.index;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.effects.RadAwayEffect;
import com.mrh0.createatomic.effects.RadioactivityEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AtomicEffects {

    private static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, CreateAtomic.MODID);

    public static final DeferredHolder<MobEffect, RadioactivityEffect> RADIOACTIVITY =
            EFFECTS.register("radioactivity", RadioactivityEffect::new);

    public static final DeferredHolder<MobEffect, RadAwayEffect> RAD_AWAY =
            EFFECTS.register("rad_away", RadAwayEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
