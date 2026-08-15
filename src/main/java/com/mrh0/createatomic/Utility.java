package com.mrh0.createatomic;

import com.mrh0.createatomic.config.AtomicConfigs;
import com.mrh0.createatomic.index.AtomicEffects;
import com.mrh0.createatomic.items.HazmatArmorItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class Utility {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static void applyRadiationInRadius(Level level, BlockPos pos, int radius, int amplifier) {
        if (!AtomicConfigs.server().radiationEffectEnabled.get()) return;
        AABB area = new AABB(pos).inflate(radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities)
            applyRadiationToEntity(entity, amplifier);
    }

    public static void applyRadiationToEntity(LivingEntity entity, int amplifier) {
        if (!AtomicConfigs.server().radiationEffectEnabled.get()) return;
        if (entity instanceof Player player && player.isCreative()) return;

        int duration = 20 * 30;
        if (entity instanceof Player player) {
            int pieces = countHazmatPieces(player);
            if (pieces >= 4) return;
            duration -= (duration / 4) * pieces;
        }

        MobEffectInstance existing = entity.getEffect(AtomicEffects.RADIOACTIVITY);
        if (existing != null && existing.getAmplifier() >= 2) return;
        int newAmplifier = existing != null ? existing.getAmplifier() + 1 : amplifier;
        entity.addEffect(new MobEffectInstance(AtomicEffects.RADIOACTIVITY, duration, newAmplifier, false, true));
    }

    public static int countHazmatPieces(Player player) {
        int count = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS)
            if (player.getItemBySlot(slot).getItem() instanceof HazmatArmorItem) count++;
        return count;
    }

    public static String format(int n) {
		if(n >= 1000_000_000)return Math.round((double)n/100_000_000d)/10d + "G";
		if(n >= 1000_000)return Math.round((double)n/100_000d)/10d + "M";
		if(n >= 1000)return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}

	public static MutableComponent getTextComponent(IEnergyStorage ies, String nan, String unit) {
		if(ies == null) return Component.literal(nan);
		return getTextComponent(ies.getEnergyStored(), unit).withStyle(ChatFormatting.AQUA).append(Component.literal(" / ").withStyle(ChatFormatting.GRAY)).append(getTextComponent(ies.getMaxEnergyStored(), unit));
	}

	public static MutableComponent getTextComponent(IEnergyStorage ies) {
		return getTextComponent(ies, "NaN", "⚡");
	}

	public static MutableComponent getTextComponent(int value, String unit) {
		return Component.literal(format(value)+unit);
	}
}
