package com.mrh0.createatomic.blocks.turbine;

import java.util.function.Predicate;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TurbineBlock extends DirectionalKineticBlock implements IWrenchable, IBE<TurbineBlockEntity> {

    // inlet  = first in chain (no same-facing turbine on intake side)
    // inline = middle in chain (same-facing turbine on both sides)
    // outlet = last in chain  (same-facing turbine on intake side, nothing on output side)
    public static final EnumProperty<TurbineType> TYPE = EnumProperty.create("type", TurbineType.class);

    public static final int placementHelperId = PlacementHelpers.register(new TurbinePlacementHelper());

    public TurbineBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(TYPE, TurbineType.INLET));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
        super.createBlockStateDefinition(builder); // adds FACING
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(TYPE) != TurbineType.OUTLET) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof TurbineBlockEntity turbine)) return;

        float speed = Math.abs(turbine.getSpeed());
        if (speed < 0.5f) return;

        Direction facing = state.getValue(FACING);

        // Particle count scales with speed: 1 at low RPM, up to 4 at 256 RPM
        int count = Math.max(1, Math.round(speed / 64f));

        for (int i = 0; i < count; i++) {
            // Spawn at the outlet face centre with a small random spread
            double spread = 0.25;
            double cx = pos.getX() + 0.5 + facing.getStepX() * 0.65;
            double cy = pos.getY() + 0.5 + facing.getStepY() * 0.65;
            double cz = pos.getZ() + 0.5 + facing.getStepZ() * 0.65;

            double x = cx + (random.nextDouble() - 0.5) * spread * (1 - Math.abs(facing.getStepX()));
            double y = cy + (random.nextDouble() - 0.5) * spread * (1 - Math.abs(facing.getStepY()));
            double z = cz + (random.nextDouble() - 0.5) * spread * (1 - Math.abs(facing.getStepZ()));

            // Velocity: outward in the facing direction + slight upward steam drift
            float velScale = speed / 1024f;
            double vx = facing.getStepX() * velScale + (random.nextDouble() - 0.5) * 0.01;
            double vy = facing.getStepY() * velScale + 0.025 + random.nextDouble() * 0.01;
            double vz = facing.getStepZ() * velScale + (random.nextDouble() - 0.5) * 0.01;

            level.addParticle(ParticleTypes.CLOUD, x, y, z, vx, vy, vz);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return state.setValue(TYPE, computeType(context.getLevel(), context.getClickedPos(), state));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        if (world.isClientSide()) return;

        // React to changes on either the intake or output side — both affect the type.
        Direction facing = state.getValue(FACING);
        boolean intakeChanged  = fromPos.equals(pos.relative(facing.getOpposite()));
        boolean outputChanged  = fromPos.equals(pos.relative(facing));
        if (!intakeChanged && !outputChanged) return;

        TurbineType newType = computeType(world, pos, state);
        if (state.getValue(TYPE) != newType)
            world.setBlock(pos, state.setValue(TYPE, newType), Block.UPDATE_ALL);
    }

    private static TurbineType computeType(LevelReader world, BlockPos pos, BlockState state) {
        Direction facing  = state.getValue(FACING);

        BlockState behind = world.getBlockState(pos.relative(facing.getOpposite()));
        boolean hasBehind = behind.getBlock() instanceof TurbineBlock
                && behind.getValue(FACING) == facing;

        if (!hasBehind) return TurbineType.INLET;

        BlockState inFront = world.getBlockState(pos.relative(facing));
        boolean hasFront   = inFront.getBlock() instanceof TurbineBlock
                && inFront.getValue(FACING) == facing;

        return hasFront ? TurbineType.INLINE : TurbineType.OUTLET;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(stack))
            return helper.getOffset(player, level, state, pos, hit)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hit);

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.SUCCESS;
    }

    @Override
    public Class<TurbineBlockEntity> getBlockEntityClass() {
        return TurbineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TurbineBlockEntity> getBlockEntityType() {
        return AtomicBlockEntities.TURBINE.get();
    }

    private static class TurbinePlacementHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.getItem() instanceof BlockItem bi
                    && bi.getBlock() instanceof TurbineBlock;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> state.getBlock() instanceof TurbineBlock;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state,
                                         BlockPos pos, BlockHitResult ray) {
            Direction facing = state.getValue(FACING);
            BlockPos target = pos.relative(facing);
            while (true) {
                BlockState at = world.getBlockState(target);
                if (!(at.getBlock() instanceof TurbineBlock)) break;
                if (at.getValue(FACING) != facing) break;
                target = target.relative(facing);
            }
            if (!world.getBlockState(target).canBeReplaced())
                return PlacementOffset.fail();

            final Direction finalFacing = facing;
            final BlockPos finalTarget  = target;
            return PlacementOffset.success(target, s -> {
                // Compute TYPE now (world state is already updated with the chain so far).
                // The new turbine is always at the end of the chain, so it's either INLET
                // (no turbine behind — shouldn't happen via helper) or OUTLET (has turbine
                // behind, nothing in front yet).
                BlockState behind   = world.getBlockState(finalTarget.relative(finalFacing.getOpposite()));
                BlockState inFront  = world.getBlockState(finalTarget.relative(finalFacing));
                boolean hasBehind   = behind.getBlock() instanceof TurbineBlock
                        && behind.getValue(FACING) == finalFacing;
                boolean hasFront    = inFront.getBlock() instanceof TurbineBlock
                        && inFront.getValue(FACING) == finalFacing;
                TurbineType type = !hasBehind ? TurbineType.INLET
                        : hasFront ? TurbineType.INLINE : TurbineType.OUTLET;
                return s.setValue(FACING, finalFacing).setValue(TYPE, type);
            });
        }
    }
}
