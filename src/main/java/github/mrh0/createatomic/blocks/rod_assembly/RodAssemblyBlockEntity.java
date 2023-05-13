package github.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import github.mrh0.createatomic.index.AtomicItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RodAssemblyBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {
    public RodAssemblyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private ItemStack currentRod = ItemStack.EMPTY;

    @Override
    public void addBehaviours(List<TileEntityBehaviour> list) {

    }

    public ItemStack getCurrentRod() {
        return currentRod;
    }

    public int getControlLevel() {
        return RodConfiguration.fromStack(currentRod).getControlLevel();
    }

    public int getFuelLevel() {
        return RodConfiguration.fromStack(currentRod).getFuelLevel();
    }

    public void updateRod(ItemStack stack) {
        if(level == null) return;

        RodConfiguration newState = RodConfiguration.fromStack(stack);
        RodAssemblyBlock.setRodState(stack, newState, getLevel(), getBlockPos());
        currentRod = stack;
    }

    public void notifyReactor() {
        if(!(level.getBlockEntity(getBlockPos().above()) instanceof ReactorCasingBlockEntity rcbe)) return;
        var con = rcbe.getControllerTE();
        if(con == null) return;
        con.onMeltdown();
    }
}
