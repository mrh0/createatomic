package com.mrh0.createatomic.items;

import com.mrh0.createatomic.config.AtomicConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class FuelRodItem extends Item {

    public FuelRodItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int fuelTicks = 0;
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom != null) fuelTicks = custom.copyTag().getInt("FuelTicks");

        int duration = Math.max(1, AtomicConfigs.server().fuelRodDuration.get());
        int remainingSeconds = Math.max(0, duration - fuelTicks) / 20;
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;

        int pct = (fuelTicks * 100) / duration;
        ChatFormatting color = pct == 0 ? ChatFormatting.GREEN : pct < 75 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        tooltip.add(Component.translatable("createatomic.tooltip.fuel_rod.depletion",
                String.format("%02dh:%02dm", hours, minutes)).withStyle(color));
    }
}
