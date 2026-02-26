package github.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.foundation.block.IBE;
import github.mrh0.createatomic.blocks.reactor_casing.AtomicConnectivityHandler;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlock;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import github.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public class RodAssemblyBlockItem extends BlockItem {

    public RodAssemblyBlockItem(Block block, Item.Properties props) {
        super(block, props);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, Player player,
                                                 ItemStack itemStack, BlockState blockState) {
        MinecraftServer minecraftserver = level.getServer();
        if (minecraftserver == null)
            return false;
        CustomData blockEntityData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            CompoundTag nbt = blockEntityData.copyTag();
            nbt.remove("Luminosity");
            nbt.remove("Size");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
            if (nbt.contains("TankContent")) {
                FluidStack fluid = FluidStack.parseOptional(minecraftserver.registryAccess(), nbt.getCompound("TankContent"));
                if (!fluid.isEmpty()) {
                    fluid.setAmount(Math.min(ReactorCasingBlockEntity.getCapacityMultiplier(), fluid.getAmount()));
                    nbt.put("TankContent", fluid.saveOptional(minecraftserver.registryAccess()));
                }
            }
            BlockEntity.addEntityType(nbt, ((IBE<?>) this.getBlock()).getBlockEntityType());
            itemStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt));
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }

    private void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        System.out.println("HERE2");
        if (player == null) return;
        if (player.isShiftKeyDown()) return;
        Direction face = ctx.getClickedFace();
        if (!face.getAxis().isVertical()) return;
        System.out.println("HERE3");
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);
        System.out.println("HERE4");
        if (!ReactorCasingBlock.isReactor(placedOnState)) return;
        ReactorCasingBlockEntity reactorAt = AtomicConnectivityHandler.partAt(AtomicBlockEntities.REACTOR_CASING.get(), world, placedOnPos);
        if (reactorAt == null) return;
        ReactorCasingBlockEntity controllerBE = reactorAt.getControllerBE();
        if (controllerBE == null) return;

        int width = controllerBE.getWidth();
        if (width == 1) return;
        System.out.println("HERE5");
        int blocksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN ? controllerBE.getBlockPos()
                .below()
                : controllerBE.getBlockPos()
                .above(controllerBE.getHeight());

        if (startPos.getY() != pos.getY()) return;
        System.out.println("HERE6");
        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                System.out.println("HERE6.1");
                if (ReactorCasingBlock.isReactor(blockState)) continue;
                System.out.println("HERE6.2");
                if (!blockState.canBeReplaced()) return;
                System.out.println("HERE6.3");
                blocksToPlace++;
            }
        }
        System.out.println("HERE7");
        if (!player.isCreative() && stack.getCount() < blocksToPlace) return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (ReactorCasingBlock.isReactor(blockState)) continue;
                BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
                player.getPersistentData()
                        .putBoolean("SilenceTankSound", true);
                super.place(context);
                player.getPersistentData()
                        .remove("SilenceTankSound");
            }
        }
    }
}