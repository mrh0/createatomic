package github.mrh0.createatomic.reactor;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MagmaticReactor implements IReactor {
    int reactorSize = 1;
    int reactorHeat = 0;
    int reactorCoolant = 0;
    @Override
    public void reactorTick(int size, int fuelRods, int controlCapacity, float controlRodInsertion) {
        reactorSize = size;
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
    public FluidStack extractFluid(boolean simulate) {
        return null;
    }

    @Override
    public FluidStack insertFluid(FluidStack stack, boolean simulate) {
        return FluidStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(boolean simulate) {
        return ItemStack.EMPTY;
    }

    private int getMaxCoolant() {
        return reactorSize * 8;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        if(!stack.is(ItemTags.STONE_CRAFTING_MATERIALS)) return stack;
        int maxInsert = Math.min(stack.getCount(), getMaxCoolant()-reactorCoolant);
        stack.shrink(maxInsert);
        return stack;
    }
}
