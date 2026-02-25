package github.mrh0.createatomic.index;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class AtomicArmInteractionPointTypes {
    private static <T extends ArmInteractionPointType> void register(String name, T type) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateAtomic.asResource(name), type);
    }

    static {
        register("rod_assembly", new RodAssemblyType());
    }

    public static class RodAssemblyType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AtomicBlocks.ROD_ASSEMBLY.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new RodAssemblyPoint(this, level, pos, state);
        }
    }

    public static class RodAssemblyPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
        public RodAssemblyPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            ItemStack input = stack.copy();
            InteractionResultHolder<ItemStack> res =
                    RodAssemblyBlock.tryInsert(cachedState, level, pos, input, false, false, simulate);
            ItemStack remainder = res.getObject();
            if (input.isEmpty()) {
                return remainder;
            } else {
                if (!simulate) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), remainder);
                return input;
            }
        }

        /*
        @Override
        public ItemStack extract(int slot, int amount, boolean simulate) {
            if (!cachedState.getOptionalValue(RodAssemblyBlock.ROD_STATE)
                    .orElse(RodConfiguration.None).isPopulated())
                return ItemStack.EMPTY;
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof RodAssemblyBlockEntity rodAssemblyBE))
                return ItemStack.EMPTY;
            ItemStack rod = rodAssemblyBE.getCurrentRod();
            if (rod.isEmpty())
                return ItemStack.EMPTY;
            if (!simulate) {
                //level.levelEvent(1010, pos, 0);
                rodAssemblyBE.updateRod(ItemStack.EMPTY);
            }
            return rod;
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            if (!RodConfiguration.isAcceptedStack(stack))
                return stack;
            if (cachedState.getOptionalValue(RodAssemblyBlock.ROD_STATE)
                    .orElse(RodConfiguration.None).isPopulated())
                return stack;
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof RodAssemblyBlockEntity rodAssemblyBE))
                return stack;
            if (!rodAssemblyBE.getCurrentRod()
                    .isEmpty())
                return stack;
            ItemStack remainder = stack.copy();
            ItemStack toInsert = remainder.split(1);
            if (!simulate) {
                rodAssemblyBE.updateRod(toInsert);
                //level.levelEvent(null, 1010, pos, Item.getId(item));
            }
            return remainder;
        }
        */
    }

    public static void register() {}
}
