package github.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import github.mrh0.createatomic.config.AtomicConfigs;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RodAssemblyBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private static int fuelDuration() { return AtomicConfigs.server().fuelRodDuration.get(); }

    private ItemStack currentRod = ItemStack.EMPTY;
    private int fuelTicks = 0;

    // Client-only: smoothly animates the rod 4px down when the reactor is running.
    public LerpedFloat insertAnimation;

    public RodAssemblyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {}

    @Override
    public void tick() {
        super.tick();
        if (level == null || !level.isClientSide()) return;
        if (insertAnimation == null)
            insertAnimation = LerpedFloat.linear().startWithValue(0f);
        insertAnimation.tickChaser();
        ReactorCasingBlockEntity reactor = findReactor();
        RodConfiguration config = getConfig();
        boolean shouldInsert;
        if (config.isControlRod()) {
            // Control rods are "inserted" (animated down) when the reactor is suppressed.
            shouldInsert = reactor != null && !reactor.isArmed();
        } else {
            // Fuel/depleted/reflector rods animate down when the reactor is running hot.
            shouldInsert = config.isPopulated() && reactor != null && reactor.getTemperature() > 25;
        }
        insertAnimation.chase(shouldInsert ? 1f : 0f, 0.15f, Chaser.EXP);
    }

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

    public RodConfiguration getConfig() {
        return RodConfiguration.fromStack(currentRod);
    }

    @Nullable
    public ReactorCasingBlockEntity findReactor() {
        if (level == null) return null;
        BlockEntity below = level.getBlockEntity(worldPosition.below());
        if (!(below instanceof ReactorCasingBlockEntity rce)) return null;
        return rce.getControllerBE();
    }

    public boolean isReactorActive() {
        ReactorCasingBlockEntity reactor = findReactor();
        return reactor != null && reactor.isActive();
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
            setChanged();
            sendData();
        }
    }

    // Returns the rod item with fuelTicks embedded so that re-inserting it later resumes depletion.
    public ItemStack getRodWithDepletion() {
        RodConfiguration config = getConfig();
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
        if (getConfig() != RodConfiguration.FuelRod) return;

        fuelTicks++;
        if (fuelTicks >= fuelDuration()) {
            fuelTicks = 0;
            updateRod(RodConfiguration.DepletedFuelRod.asStack());
        }
    }

    public int getControlLevel() {
        return getConfig().getControlLevel();
    }

    public int getFuelLevel() {
        return getConfig().getFuelLevel();
    }

    public int getFuelProgress() {
        return fuelTicks;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        RodConfiguration config = getConfig();
        String spacing = "  ";
        tooltip.add(Component.literal(spacing).append(
                Component.translatable("block.createatomic.rod_assembly").withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal(spacing + " ").append(config.getTooltip().withStyle(ChatFormatting.GRAY)));
        if (config == RodConfiguration.FuelRod) {
            int pct = (fuelTicks * 100) / fuelDuration();
            tooltip.add(Component.literal(spacing + " ").append(
                    Component.literal(pct + "% depleted").withStyle(ChatFormatting.YELLOW)));
        }
        if (config.isLockedWhileRunning() && isReactorActive()) {
            tooltip.add(Component.literal(spacing + " ").append(
                    Component.translatable("createatomic.tooltip.rod_assembly.locked").withStyle(ChatFormatting.RED)));
        }
        return true;
    }
}
