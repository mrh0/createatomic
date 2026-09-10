package com.mrh0.createatomic.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// Pure status marker - Utility.applyRadiationToEntity checks for its presence and skips
// applying Radioactivity while it's active. It has no tick behaviour of its own.
public class RadAwayEffect extends MobEffect {

    public RadAwayEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x4FA8E0);
    }
}
