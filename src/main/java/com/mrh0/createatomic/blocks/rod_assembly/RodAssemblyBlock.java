package com.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.mrh0.createatomic.Utility;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.mrh0.createatomic.config.AtomicConfigs;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
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

import java.util.function.BiConsumer;

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
        if (rabe.isLockedWith(incoming))
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

        if (rabe.isLocked())
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
            if (rabe.isLocked()) {
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
            if (rabe.isLockedWith(incoming)) {
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
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RodAssemblyBlockEntity rabe)
            dropRodAndCheckMeltdown(level, pos, rabe);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion,
                               BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RodAssemblyBlockEntity rabe)
            dropRodAndCheckMeltdown(level, pos, rabe);
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    private static void dropRodAndCheckMeltdown(Level level, BlockPos pos, RodAssemblyBlockEntity rabe) {
        var controller = rabe.findReactor();
        boolean inMeltdown = controller != null && controller.hasMeltdown;

        if (!inMeltdown) {
            ItemStack rod = rabe.getRodWithDepletion();
            if (!rod.isEmpty())
                popResource(level, pos, rod);
        }

        if (!inMeltdown && controller != null && controller.shouldMeltdownOnBreak())
            controller.onMeltdown();
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof RodAssemblyBlockEntity rabe) {
            ReactorCasingBlockEntity controller = rabe.findReactor();
            if (controller != null && controller.isActive()) {
                Player player = context.getPlayer();
                if (player != null)
                    player.displayClientMessage(
                        Component.translatable("createatomic.message.wrench_active_reactor").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }
        }
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public Class<RodAssemblyBlockEntity> getBlockEntityClass() {
        return RodAssemblyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RodAssemblyBlockEntity> getBlockEntityType() {
        return AtomicBlockEntities.ROD_ASSEMBLY.get();
    }

    private static final int RADIUS = 10;

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!AtomicConfigs.server().blockRadiationEnabled.get()) return;
        if (!(level.getBlockEntity(pos) instanceof RodAssemblyBlockEntity be)) return;
        if (be.getConfig() != RodConfiguration.FuelRod) return;
        Utility.applyRadiationInRadius(level, pos, RADIUS, 0);
    }
}
