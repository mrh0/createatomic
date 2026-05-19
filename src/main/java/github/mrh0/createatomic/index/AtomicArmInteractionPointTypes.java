package github.mrh0.createatomic.index;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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

    public static class RodAssemblyPoint extends ArmInteractionPoint {
        public RodAssemblyPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            InteractionResultHolder<ItemStack> res =
                    RodAssemblyBlock.tryInsert(cachedState, level, pos, stack.copy(), false, false, simulate);
            if (res.getResult().consumesAction())
                return res.getObject();
            return stack;
        }

        @Override
        public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
            return RodAssemblyBlock.tryExtract(cachedState, level, pos, simulate);
        }

        @Override
        public int getSlotCount(ArmBlockEntity armBlockEntity) {
            return 1;
        }
    }

    public static void register() {}
}
