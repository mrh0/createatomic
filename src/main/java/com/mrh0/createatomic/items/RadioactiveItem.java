package com.mrh0.createatomic.items;

import com.mrh0.createatomic.Utility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// An item that occasionally irradiates the player carrying it.
public class RadioactiveItem extends Item {

    private static final float RADIATION_CHANCE = 0.002f;

    public RadioactiveItem(Properties properties) {
        super(properties);
    }

    public static void irradiateHolder(Level level, Entity entity) {
        if (level.isClientSide || !(entity instanceof Player player)) return;
        if (player.getRandom().nextFloat() < RADIATION_CHANCE)
            Utility.applyRadiationToEntity(player, 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        irradiateHolder(level, entity);
    }
}
