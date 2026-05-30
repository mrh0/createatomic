package com.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RodAssemblyBlock extends Block implements IWrenchable, IBE<RodAssemblyBlockEntity>, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public RodAssemblyBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // Used by mechanical arm to insert rods.
    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
        if (!RodConfiguration.isAcceptedStack(stack))
            return InteractionResultHolder.pass(stack);

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof RodAssemblyBlockEntity rabe))
            return InteractionResultHolder.pass(stack);

        if (rabe.getConfig().isPopulated())
            return InteractionResultHolder.pass(stack);

        RodConfiguration incoming = RodConfiguration.fromStack(stack);
        if (incoming.isLockedWhileRunning() && rabe.isReactorActive())
            return InteractionResultHolder.pass(stack);

        ItemStack remainder = stack.copy();
        ItemStack toInsert = remainder.split(1);
        if (!simulate)
            rabe.updateRod(toInsert);
        return InteractionResultHolder.success(remainder);
    }

    // Used by mechanical arm to extract rods.
    public static ItemStack tryExtract(BlockState state, Level world, BlockPos pos, boolean simulate) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof RodAssemblyBlockEntity rabe))
            return ItemStack.EMPTY;

        RodConfiguration config = rabe.getConfig();
        if (!config.isPopulated())
            return ItemStack.EMPTY;

        if (config.isLockedWhileRunning() && rabe.isReactorActive())
            return ItemStack.EMPTY;

        ItemStack rod = rabe.getRodWithDepletion();
        if (!simulate)
            rabe.updateRod(ItemStack.EMPTY);
        return rod;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RodAssemblyBlockEntity rabe))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        RodConfiguration currentConfig = rabe.getConfig();

        // Empty hand: extract rod if present
        if (stack.isEmpty()) {
            if (!currentConfig.isPopulated())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (currentConfig.isLockedWhileRunning() && rabe.isReactorActive()) {
                player.displayClientMessage(
                        Component.translatable("createatomic.message.rod_locked").withStyle(ChatFormatting.RED), true);
                return ItemInteractionResult.FAIL;
            }
            player.getInventory().placeItemBackInInventory(rabe.getRodWithDepletion());
            rabe.updateRod(ItemStack.EMPTY);
            return ItemInteractionResult.SUCCESS;
        }

        // Holding a rod item: insert if slot is empty
        if (RodConfiguration.isAcceptedStack(stack)) {
            if (currentConfig.isPopulated())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            RodConfiguration incoming = RodConfiguration.fromStack(stack);
            if (incoming.isLockedWhileRunning() && rabe.isReactorActive()) {
                player.displayClientMessage(
                        Component.translatable("createatomic.message.rod_locked").withStyle(ChatFormatting.RED), true);
                return ItemInteractionResult.FAIL;
            }
            ItemStack toInsert = stack.copyWithCount(1);
            if (!player.isCreative()) stack.shrink(1);
            rabe.updateRod(toInsert);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RodAssemblyBlockEntity rabe && !level.isClientSide()) {
                var controller = rabe.findReactor();
                boolean inMeltdown = controller != null && controller.hasMeltdown;

                // Drop the rod only when broken normally — rods are destroyed in a meltdown.
                if (!inMeltdown) {
                    ItemStack rod = rabe.getRodWithDepletion();
                    if (!rod.isEmpty())
                        popResource(level, pos, rod);
                }

                if (!inMeltdown && controller != null && controller.shouldMeltdownOnBreak())
                    controller.onMeltdown();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public Class<RodAssemblyBlockEntity> getBlockEntityClass() {
        return RodAssemblyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RodAssemblyBlockEntity> getBlockEntityType() {
        return AtomicBlockEntities.ROD_ASSEMBLY.get();
    }
}
