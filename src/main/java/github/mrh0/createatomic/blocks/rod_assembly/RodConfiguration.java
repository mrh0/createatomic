package github.mrh0.createatomic.blocks.rod_assembly;

import github.mrh0.createatomic.index.AtomicItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public enum RodConfiguration implements StringRepresentable {
    None("none"),
    SmallControlRod("small_control_rod"),
    LargeControlRod("large_control_rod"),
    SmallFuelRod("small_fuel_rod"),
    LargeFuelRod("large_fuel_rod");

    private String name;

    private RodConfiguration(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public MutableComponent getTooltip() {
        switch (this) {
            case SmallControlRod:
                return new TranslatableComponent("createatomic.tooltip.rod_assembly.small_control_rod");
            case LargeControlRod:
                return new TranslatableComponent("createatomic.tooltip.rod_assembly.large_control_rod");
            case SmallFuelRod:
                return new TranslatableComponent("createatomic.tooltip.rod_assembly.small_fuel_rod");
            case LargeFuelRod:
                return new TranslatableComponent("createatomic.tooltip.rod_assembly.large_fuel_rod");
        }
        return new TranslatableComponent("createatomic.tooltip.rod_assembly.none");
    }

    public ItemStack getItemStack() {
        switch (this) {
            case SmallControlRod:
                return AtomicItems.SMALL_CONTROL_ROD.asStack();
            case LargeControlRod:
                return AtomicItems.LARGE_CONTROL_ROD.asStack();
            case SmallFuelRod:
                return AtomicItems.SMALL_FUEL_ROD.asStack();
            case LargeFuelRod:
                return AtomicItems.LARGE_FUEL_ROD.asStack();
        }
        return ItemStack.EMPTY;
    }

    public boolean isPopulated() {
        return this != None;
    }
}
