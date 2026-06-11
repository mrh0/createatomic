package com.mrh0.createatomic.items;

import com.mrh0.createatomic.index.AtomicEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
