package com.mrh0.createatomic.effects;

import net.minecraft.tags.EntityTypeTags;
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
        if (entity.getType().is(EntityTypeTags.UNDEAD)) {
            if (!entity.hasEffect(MobEffects.REGENERATION))
                entity.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 20, amplifier, false, false), null);
            if (!entity.hasEffect(MobEffects.DAMAGE_BOOST))
                entity.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 20, amplifier, false, false), null);
            if (!entity.hasEffect(MobEffects.MOVEMENT_SPEED))
                entity.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, amplifier, false, false), null);
            return true;
        }

        if (!entity.hasEffect(MobEffects.HUNGER))
            entity.forceAddEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 20, amplifier + 1, false, false), null);

        RandomSource random = entity.getRandom();
        if (amplifier >= 1 && !entity.hasEffect(MobEffects.CONFUSION) && random.nextFloat() < 0.3f)
            entity.forceAddEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 10, 0, false, false), null);
        if (amplifier >= 2 && !entity.hasEffect(MobEffects.BLINDNESS) && random.nextFloat() < 0.1f)
            entity.forceAddEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 10, 0, false, false), null);

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 200 == 0;
    }
}
