package github.mrh0.createatomic.blocks.turbine;

import java.util.function.Predicate;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import github.mrh0.createatomic.index.AtomicBlockEntities;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TurbineBlock extends DirectionalKineticBlock implements IWrenchable, IBE<TurbineBlockEntity> {

    // Registered once at class-load time; the ID is used in useItemOn to retrieve the helper.
    public static final int placementHelperId = PlacementHelpers.register(new TurbinePlacementHelper());

    public TurbineBlock(Properties props) {
        super(props);
    }

    // Shaft runs through on the FACING axis; FACING = shaft output, FACING.opposite = reactor intake.
    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
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

    // ── Placement helper ─────────────────────────────────────────────────────────
    // Mirrors Create's PoleHelper / ShaftBlock pattern.
    // When the player right-clicks a turbine while holding another turbine, the new
    // turbine is placed at the far end of the existing chain in the FACING direction.
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

            // Walk forward along the FACING direction past any same-facing turbines.
            BlockPos target = pos.relative(facing);
            while (true) {
                BlockState at = world.getBlockState(target);
                if (!(at.getBlock() instanceof TurbineBlock)) break;
                if (at.getValue(FACING) != facing) break;
                target = target.relative(facing);
            }

            // Place at target only if the position is replaceable.
            if (!world.getBlockState(target).canBeReplaced())
                return PlacementOffset.fail();

            Direction finalFacing = facing;
            return PlacementOffset.success(target,
                    s -> s.setValue(FACING, finalFacing));
        }
    }
}
