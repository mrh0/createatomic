package github.mrh0.createatomic.reactor;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IReactor {
    void reactorTick(int size, int fuelRods, int controlCapacity, float controlRodInsertion);
    int getHeat();
    void setHeat(int heat);
    int getCoolant();
    void setCoolant(int coolant);
    FluidStack extractFluid(boolean simulate);
    FluidStack insertFluid(FluidStack stack, boolean simulate);
    ItemStack extractItem(boolean simulate);
    ItemStack insertItem(ItemStack stack, boolean simulate);
    default boolean isActive() {
        return getHeat() > 0;
    }
}
