package github.mrh0.createatomic.index;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlock;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;
import github.mrh0.createatomic.blocks.rod_assembly.RodConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class AtomicArmInteractionPointTypes {
    public static final RodAssemblyType ROD_ASSEMBLY = register("rod_assembly", RodAssemblyType::new);

    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(CreateAtomic.asResource(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static class RodAssemblyType extends ArmInteractionPointType {
        public RodAssemblyType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AtomicBlocks.ROD_ASSEMBLY.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new RodAssemblyPoint(this, level, pos, state);
        }
    }

    public static class RodAssemblyPoint extends AllArmInteractionPointTypes.TopFaceArmInteractionPoint {
        public RodAssemblyPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public int getSlotCount() {
            return 1;
        }

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
        public ItemStack insert(ItemStack stack, boolean simulate) {
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
    }

    public static void register() {}
}
