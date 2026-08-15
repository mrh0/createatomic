package com.mrh0.createatomic.items;

import com.mrh0.createatomic.blocks.rod_assembly.RodConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RodItem extends Item {

    public RodItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        RodConfiguration config = RodConfiguration.fromStack(stack);
        if (config.effectivePower > 0)
            tooltip.add(Component.translatable("createatomic.tooltip.rod_item.power").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(config.effectivePower)).withStyle(ChatFormatting.GREEN)));
        if (config.controlLevel > 0)
            tooltip.add(Component.translatable("createatomic.tooltip.rod_item.control_capacity").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("+" + config.controlLevel).withStyle(ChatFormatting.AQUA)));
        if (config.controlLevel < 0)
            tooltip.add(Component.translatable("createatomic.tooltip.rod_item.control_capacity").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(config.controlLevel)).withStyle(ChatFormatting.RED)));
        if (config.adjacencyBonus > 0)
            tooltip.add(Component.translatable("createatomic.tooltip.rod_item.adjacency").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("+%.0f%%", config.adjacencyBonus * 100)).withStyle(ChatFormatting.YELLOW)));
        if (config.adjacentFuelConsumptionBonus > 0)
            tooltip.add(Component.translatable("createatomic.tooltip.rod_item.fuel_lifetime").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("-%.0f%%", config.adjacentFuelConsumptionBonus * 100)).withStyle(ChatFormatting.GOLD)));
        if (config.capacityBuff > 0)
            tooltip.add(Component.translatable("createatomic.tooltip.rod_item.capacity_buff").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("+" + config.capacityBuff).withStyle(ChatFormatting.AQUA)));
        if (config.capacityBuff < 0)
            tooltip.add(Component.translatable("createatomic.tooltip.rod_item.capacity_buff").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(config.capacityBuff)).withStyle(ChatFormatting.RED)));
    }
}
