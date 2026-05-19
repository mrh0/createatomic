package github.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import github.mrh0.createatomic.index.AtomicBlockEntities;
import github.mrh0.createatomic.index.AtomicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
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

    // Used by mechanical arm to insert rods.
    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
        if (!RodConfiguration.isAcceptedStack(stack))
            return InteractionResultHolder.pass(stack);

        RodConfiguration currentConfig = state.getOptionalValue(ROD_STATE).orElse(RodConfiguration.None);
        if (currentConfig.isPopulated())
            return InteractionResultHolder.pass(stack);

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof RodAssemblyBlockEntity rabe))
            return InteractionResultHolder.pass(stack);

        ItemStack remainder = stack.copy();
        ItemStack toInsert = remainder.split(1);
        if (!simulate)
            rabe.updateRod(toInsert);
        return InteractionResultHolder.success(remainder);
    }

    // Used by mechanical arm to extract rods.
    public static ItemStack tryExtract(BlockState state, Level world, BlockPos pos, boolean simulate) {
        RodConfiguration currentConfig = state.getOptionalValue(ROD_STATE).orElse(RodConfiguration.None);
        if (!currentConfig.isPopulated())
            return ItemStack.EMPTY;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof RodAssemblyBlockEntity rabe))
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

        RodConfiguration currentConfig = state.getValue(ROD_STATE);

        // Empty hand: extract rod (with depletion progress embedded) if one is present
        if (stack.isEmpty()) {
            if (!currentConfig.isPopulated())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            player.getInventory().placeItemBackInInventory(rabe.getRodWithDepletion());
            rabe.updateRod(ItemStack.EMPTY);
            return ItemInteractionResult.SUCCESS;
        }

        // Holding a rod item: insert if slot is empty
        if (RodConfiguration.isAcceptedStack(stack)) {
            if (currentConfig.isPopulated())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            ItemStack toInsert = stack.copyWithCount(1);
            if (!player.isCreative()) stack.shrink(1);
            rabe.updateRod(toInsert);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static void setRodState(ItemStack stack, RodConfiguration rod, Level level, BlockPos pos) {
        level.setBlock(pos, AtomicBlocks.ROD_ASSEMBLY.getDefaultState().setValue(ROD_STATE, rod), Block.UPDATE_ALL);
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
