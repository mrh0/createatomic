package com.mrh0.createatomic.items;

import com.mrh0.createatomic.Utility;
import com.mrh0.createatomic.config.AtomicConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

public class FuelRodItem extends RodItem {

    private static final float RADIATION_CHANCE = 0.002f;

    public FuelRodItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;
        if (player.getRandom().nextFloat() < RADIATION_CHANCE)
            Utility.applyRadiationToEntity(player, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int fuelTicks = 0;
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom != null) fuelTicks = custom.copyTag().getInt("FuelTicks");

        int duration = Math.max(1, AtomicConfigs.server().fuelRodDuration.get());
        int remainingSeconds = Math.max(0, duration - fuelTicks) / 20;
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;

        int pct = (fuelTicks * 100) / duration;
        ChatFormatting color = pct == 0 ? ChatFormatting.GREEN : pct < 75 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        tooltip.add(Component.translatable("createatomic.tooltip.fuel_rod.depletion",
                String.format("%02dh:%02dm:%02ds", hours, minutes, seconds)).withStyle(color));
    }
}
