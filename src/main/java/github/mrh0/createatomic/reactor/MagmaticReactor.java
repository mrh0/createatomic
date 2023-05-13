package github.mrh0.createatomic.reactor;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MagmaticReactor implements IReactor{
    @Override
    public void reactorTick(int size, int fuelRods, int controlCapacity, float controlRodInsertion) {

    }

    @Override
    public int getHeat() {
        return 0;
    }

    @Override
    public void setHeat(int heat) {

    }

    @Override
    public int getCoolant() {
        return 0;
    }

    @Override
    public void setCoolant(int coolant) {

    }

    @Override
    public FluidStack extractFluid() {
        return null;
    }

    @Override
    public void insertFluid(FluidStack stack) {

    }

    @Override
    public ItemStack extractItem() {
        return null;
    }

    @Override
    public void insertItem(ItemStack stack) {

    }
}
