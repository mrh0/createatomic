package com.mrh0.createatomic.items;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrh0.createatomic.index.AtomicEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class HazmatArmorItem extends ArmorItem {

    public HazmatArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    private static boolean isFullSetWorn(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof HazmatArmorItem
            && entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof HazmatArmorItem
            && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof HazmatArmorItem
            && entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof HazmatArmorItem;
    }

    private static final ResourceLocation HAZMAT_OVERLAY =
            ResourceLocation.fromNamespaceAndPath("createatomic", "textures/misc/hazmat.png");

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public void renderHelmetOverlay(ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
                if (type != Type.HELMET) return;
                Minecraft mc = Minecraft.getInstance();
                int w = mc.getWindow().getGuiScaledWidth();
                int h = mc.getWindow().getGuiScaledHeight();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                guiGraphics.blit(HAZMAT_OVERLAY, 0, 0, 0, 0.0f, 0.0f, w, h, w, h);
                RenderSystem.disableBlend();
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide || this.type != Type.HELMET) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (isFullSetWorn(living)) {
            living.removeEffect(AtomicEffects.RADIOACTIVITY);
        }
    }
}
