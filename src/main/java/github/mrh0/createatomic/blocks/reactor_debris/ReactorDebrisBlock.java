package github.mrh0.createatomic.blocks.reactor_debris;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
        AABB area = new AABB(pos).inflate(RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            if (entity instanceof Player player && player.isCreative()) continue;
            // Hunger (radiation sickness) always applied
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20*60, 1, false, true));

            // Nausea 30% chance per tick
            if (random.nextFloat() < 0.3f)
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20*15, 0, false, true));

            // Blindness 10% chance per tick
            if (random.nextFloat() < 0.1f)
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20*10, 0, false, true));
        }
    }
}
