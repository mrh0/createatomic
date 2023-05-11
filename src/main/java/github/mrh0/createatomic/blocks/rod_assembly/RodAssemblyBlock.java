package github.mrh0.createatomic.blocks.rod_assembly;

import github.mrh0.createatomic.index.AtomicBlocks;
import github.mrh0.createatomic.index.AtomicItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class RodAssemblyBlock extends Block {

    public static final EnumProperty<RodConfiguration> ROD_STATE = EnumProperty.create("rod", RodConfiguration.class);

    public RodAssemblyBlock(Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROD_STATE);
    }

    public InteractionResult use(BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        RodConfiguration rod = state.getValue(ROD_STATE);

        if(rod.isPopulated()) {
            if(stack.isEmpty()) {
                setRodState(RodConfiguration.None, level, pos);
                player.setItemInHand(hand, rod.getItemStack());
            }
            return InteractionResult.SUCCESS;
        }

        if(stack.isEmpty()) return InteractionResult.PASS;

        if(stack.is(AtomicItems.SMALL_CONTROL_ROD.get())) {
            setRodState(RodConfiguration.SmallControlRod, level, pos);
            return InteractionResult.SUCCESS;
        }
        if(stack.is(AtomicItems.LARGE_CONTROL_ROD.get())) {
            setRodState(RodConfiguration.LargeControlRod, level, pos);
            return InteractionResult.SUCCESS;
        }
        if(stack.is(AtomicItems.SMALL_FUEL_ROD.get())) {
            setRodState(RodConfiguration.SmallFuelRod, level, pos);
            return InteractionResult.SUCCESS;
        }
        if(stack.is(AtomicItems.SMALL_FUEL_ROD.get())) {
            setRodState(RodConfiguration.SmallFuelRod, level, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void setRodState(RodConfiguration rod, Level level, BlockPos pos) {
        level.setBlock(pos, AtomicBlocks.ROD_ASSEMBLY.getDefaultState().setValue(ROD_STATE, rod), Block.UPDATE_ALL);
        // TODO: Alert below reactor
    }
}
