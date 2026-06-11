package com.mrh0.createatomic.blocks.reactor_debris;

import com.mrh0.createatomic.config.AtomicConfigs;
import com.mrh0.createatomic.index.AtomicEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ReactorDebrisBlock extends Block {

    private static final int RADIUS = 16;

    public ReactorDebrisBlock(Properties props) {
        super(props);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!AtomicConfigs.server().debrisRadiation.get()) return;
        AABB area = new AABB(pos).inflate(RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            if (entity instanceof Player player && player.isCreative()) continue;
            entity.addEffect(new MobEffectInstance(AtomicEffects.RADIOACTIVITY, 20 * 15, 0, false, true));
        }
    }
}
