package com.mrh0.createatomic.items;

import com.mrh0.createatomic.index.AtomicEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class RadPillItem extends Item {

    private static final int USE_DURATION = 16;
    private static final int IMMUNITY_DURATION = 20 * 60 * 10; // 10 minutes

    public RadPillItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (entityLiving instanceof ServerPlayer serverPlayer)
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));

        if (!level.isClientSide()) {
            entityLiving.removeEffect(AtomicEffects.RADIOACTIVITY);
            entityLiving.addEffect(new MobEffectInstance(AtomicEffects.RAD_AWAY, IMMUNITY_DURATION, 0, false, false, true));
        }

        stack.consume(1, entityLiving);
        return stack;
    }
}
