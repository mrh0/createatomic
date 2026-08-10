package com.mrh0.createatomic.blocks.reactor_casing;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class ReactorCasingBlock extends Block implements IWrenchable, IBE<ReactorCasingBlockEntity> {

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        // AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public static boolean isReactor(BlockState state) {
        return state.getBlock() instanceof ReactorCasingBlock;
    }

    public ReactorCasingBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true));
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, ReactorCasingBlockEntity::updateConnectivity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        ReactorCasingBlockEntity controller = getBlockEntityOptional(context.getLevel(), context.getClickedPos())
                .map(ReactorCasingBlockEntity::getControllerBE).orElse(null);
        if (controller != null && controller.isActive()) {
            Player player = context.getPlayer();
            if (player != null)
                player.displayClientMessage(
                    Component.translatable("createatomic.message.wrench_active_reactor").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    static final VoxelShape CAMPFIRE_SMOKE_CLIP = Block.box(0, 4, 0, 16, 16, 16);

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
                                        CollisionContext pContext) {
        if (pContext == CollisionContext.empty())
            return CAMPFIRE_SMOKE_CLIP;
        return pState.getShape(pLevel, pPos);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.block();
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity te = world.getBlockEntity(pos);
            if (!(te instanceof ReactorCasingBlockEntity rcbe)) {
                world.removeBlockEntity(pos);
                return;
            }

            // Trigger meltdown if the reactor is active or dangerously hot when a casing is broken
            boolean triggeredMeltdown = false;
            if (!world.isClientSide()) {
                ReactorCasingBlockEntity controller = rcbe.getControllerBE();
                if (controller != null && controller.shouldMeltdownOnBreak()) {
                    controller.onMeltdown();
                    triggeredMeltdown = true;
                }
            }

            world.removeBlockEntity(pos);
            if (!triggeredMeltdown)
                AtomicConnectivityHandler.splitMulti(rcbe);
        }
    }

    @Override
    public Class<ReactorCasingBlockEntity> getBlockEntityClass() {
        return ReactorCasingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ReactorCasingBlockEntity> getBlockEntityType() {
        return AtomicBlockEntities.REACTOR_CASING.get();
    }

    // Tanks are less noisy when placed in batch
    public static final SoundType SILENCED_METAL =
            new DeferredSoundType(0.1F, 1.5F, () -> SoundEvents.METAL_BREAK, () -> SoundEvents.METAL_STEP,
                    () -> SoundEvents.METAL_PLACE, () -> SoundEvents.METAL_HIT, () -> SoundEvents.METAL_FALL);

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData()
                .contains("SilenceTankSound"))
            return SILENCED_METAL;
        return soundType;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return getBlockEntityOptional(worldIn, pos).map(ReactorCasingBlockEntity::getControllerBE)
                .map(te -> ComparatorUtil.fractionToRedstoneLevel(te.getFillState()))
                .orElse(0);
    }
}
