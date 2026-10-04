package com.mrh0.createatomic.client;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.datagen.TagProvider.CATagRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = CreateAtomic.MODID, value = Dist.CLIENT)
public class AtomicClientEvents {

    // Lowest priority so the line lands below Create's item descriptions.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().is(CATagRegister.Items.RADIOACTIVE))
            event.getToolTip().add(Component.translatable("createatomic.tooltip.radioactive").withStyle(ChatFormatting.GREEN));
    }
}
