package com.mrh0.createatomic.blocks.nuclear_bomb;

import com.mrh0.createatomic.entity.NuclearBombEntity;
import com.mrh0.createatomic.index.AtomicEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class NuclearBombBlock extends Block {

    public NuclearBombBlock(Properties props) {
        super(props);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.FLINT_AND_STEEL)) {
            prime(level, pos, player);
            level.removeBlock(pos, false);
            if (!player.isCreative()) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.is(Items.FIRE_CHARGE)) {
            prime(level, pos, player);
            level.removeBlock(pos, false);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                   BlockPos neighborPos, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos)) {
            prime(level, pos, null);
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion,
                               BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (!level.isClientSide) {
            NuclearBombEntity entity = new NuclearBombEntity(
                    AtomicEntities.NUCLEAR_BOMB.get(), level,
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    explosion.getIndirectSourceEntity());
            int fuse = entity.getFuse();
            entity.setFuse(level.random.nextInt(fuse / 4) + fuse / 8);
            level.addFreshEntity(entity);
        }
    }

    @Override
    public void onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face,
                             @Nullable LivingEntity igniter) {
        prime(world, pos, igniter);
        world.removeBlock(pos, false);
    }

    public static void prime(Level level, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            NuclearBombEntity entity = new NuclearBombEntity(
                    AtomicEntities.NUCLEAR_BOMB.get(), level,
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    igniter);
            level.addFreshEntity(entity);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 0.8f);
            level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }
}
