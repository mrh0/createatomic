package github.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import github.mrh0.createatomic.index.AtomicBlockEntities;
import github.mrh0.createatomic.index.AtomicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RodAssemblyBlock extends Block implements IWrenchable, IBE<RodAssemblyBlockEntity> {

    public static final EnumProperty<RodConfiguration> ROD_STATE = EnumProperty.create("rod", RodConfiguration.class);
    public static VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public RodAssemblyBlock(Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROD_STATE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
        return InteractionResultHolder.pass(ItemStack.EMPTY);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide()) return ItemInteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof RodAssemblyBlockEntity rabe)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /*
    @Override
    public InteractionResult use(BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof RodAssemblyBlockEntity rabe)) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);
        RodConfiguration rod = state.getValue(ROD_STATE);
        rabe.updateRod(stack);

        if(rod.isPopulated()) {
            if(player.isCrouching())
                return InteractionResult.PASS;
            if(stack.isEmpty()) {
                setRodState(stack, RodConfiguration.None, level, pos);
                player.setItemInHand(hand, rod.asStack());
            }
            return InteractionResult.SUCCESS;
        }

        if(stack.isEmpty()) return InteractionResult.PASS;



        return InteractionResult.PASS;
    }
    */

    public static void setRodState(ItemStack stack, RodConfiguration rod, Level level, BlockPos pos) {
        level.setBlock(pos, AtomicBlocks.ROD_ASSEMBLY.getDefaultState().setValue(ROD_STATE, rod), Block.UPDATE_ALL);
        // TODO: Alert below reactor
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
