package com.mrh0.createatomic.effects;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class RadioactivityEffect extends MobEffect {

    public RadioactivityEffect() {
        super(MobEffectCategory.HARMFUL, 0x78BE20);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.forceAddEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 20, 1 + amplifier, false, false), null);

        RandomSource random = entity.getRandom();
        if (amplifier >= 1 && random.nextFloat() < 0.3f)
            entity.forceAddEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 10, 0, false, false), null);
        if (amplifier >= 2 && random.nextFloat() < 0.1f)
            entity.forceAddEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 10, 0, false, false), null);

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 200 == 0;
    }
}
