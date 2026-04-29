package github.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RodAssemblyBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public RodAssemblyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private ItemStack currentRod = ItemStack.EMPTY;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {

    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("rod", currentRod.save(registries));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        currentRod = ItemStack.EMPTY;
        if (tag.contains("rod")) {
            currentRod = ItemStack.parse(registries, tag.getCompound("rod")).orElse(ItemStack.EMPTY);
        }
    }

    public ItemStack getCurrentRod() {
        return ItemStack.EMPTY;
    }

    public int getControlLevel() {
        return getBlockState().getOptionalValue(RodAssemblyBlock.ROD_STATE).orElse(RodConfiguration.None).getControlLevel();
    }

    public int getFuelLevel() {
        return getBlockState().getOptionalValue(RodAssemblyBlock.ROD_STATE).orElse(RodConfiguration.None).getFuelLevel();
    }
}
