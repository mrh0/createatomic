package github.mrh0.createatomic.reactor;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IReactor {
    void reactorTick(int size, int fuelRods, int controlCapacity, float controlRodInsertion);
    int getHeat();
    void setHeat(int heat);
    int getCoolant();
    void setCoolant(int coolant);
    FluidStack extractFluid();
    FluidStack insertFluid(FluidStack stack);
    ItemStack extractItem();
    ItemStack insertItem(ItemStack stack);
}
