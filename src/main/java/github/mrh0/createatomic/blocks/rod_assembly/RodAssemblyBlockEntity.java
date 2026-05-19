package github.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RodAssemblyBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    // Ticks a fuel rod lasts before becoming depleted (20 minutes)
    private static final int FUEL_DURATION = 24000;

    private ItemStack currentRod = ItemStack.EMPTY;
    private int fuelTicks = 0;

    public RodAssemblyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {}

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!currentRod.isEmpty())
            tag.put("rod", currentRod.save(registries));
        tag.putInt("fuelTicks", fuelTicks);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        currentRod = ItemStack.EMPTY;
        if (tag.contains("rod"))
            currentRod = ItemStack.parse(registries, tag.getCompound("rod")).orElse(ItemStack.EMPTY);
        fuelTicks = tag.getInt("fuelTicks");
    }

    public ItemStack getCurrentRod() {
        return currentRod.copy();
    }

    public void updateRod(ItemStack stack) {
        currentRod = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);

        // Restore depletion progress that was embedded on the item when it was last extracted.
        fuelTicks = 0;
        if (!currentRod.isEmpty()) {
            CustomData custom = currentRod.get(DataComponents.CUSTOM_DATA);
            if (custom != null) fuelTicks = custom.copyTag().getInt("FuelTicks");
        }

        if (hasLevel() && !level.isClientSide()) {
            RodConfiguration config = RodConfiguration.fromStack(currentRod);
            level.setBlock(worldPosition, getBlockState().setValue(RodAssemblyBlock.ROD_STATE, config), Block.UPDATE_ALL);
            setChanged();
            sendData();
        }
    }

    // Returns the rod item for the current configuration with fuelTicks embedded so that
    // re-inserting it later resumes from the same depletion level.
    public ItemStack getRodWithDepletion() {
        RodConfiguration config = getBlockState().getOptionalValue(RodAssemblyBlock.ROD_STATE)
                .orElse(RodConfiguration.None);
        ItemStack rod = config.asStack();
        if (rod.isEmpty()) return ItemStack.EMPTY;
        if (config == RodConfiguration.FuelRod && fuelTicks > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("FuelTicks", fuelTicks);
            rod.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return rod;
    }

    // Called every lazy tick by the reactor controller to advance fuel consumption.
    public void tickRod() {
        if (level == null || level.isClientSide()) return;
        RodConfiguration config = getBlockState().getOptionalValue(RodAssemblyBlock.ROD_STATE)
                .orElse(RodConfiguration.None);
        if (config != RodConfiguration.FuelRod) return;

        fuelTicks++;
        if (fuelTicks >= FUEL_DURATION) {
            fuelTicks = 0;
            updateRod(RodConfiguration.DepletedFuelRod.asStack());
        }
    }

    public int getControlLevel() {
        return getBlockState().getOptionalValue(RodAssemblyBlock.ROD_STATE)
                .orElse(RodConfiguration.None).getControlLevel();
    }

    public int getFuelLevel() {
        return getBlockState().getOptionalValue(RodAssemblyBlock.ROD_STATE)
                .orElse(RodConfiguration.None).getFuelLevel();
    }

    public int getFuelProgress() {
        return fuelTicks;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        RodConfiguration config = getBlockState().getOptionalValue(RodAssemblyBlock.ROD_STATE)
                .orElse(RodConfiguration.None);
        String spacing = "  ";
        tooltip.add(Component.literal(spacing).append(
                Component.translatable("block.createatomic.rod_assembly").withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal(spacing + " ").append(config.getTooltip().withStyle(ChatFormatting.GRAY)));
        if (config == RodConfiguration.FuelRod) {
            int pct = (fuelTicks * 100) / FUEL_DURATION;
            tooltip.add(Component.literal(spacing + " ").append(
                    Component.literal(pct + "% depleted").withStyle(ChatFormatting.YELLOW)));
        }
        return true;
    }
}
