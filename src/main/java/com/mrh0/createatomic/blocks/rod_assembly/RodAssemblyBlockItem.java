package com.mrh0.createatomic.blocks.rod_assembly;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlock;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RodAssemblyBlockItem extends BlockItem {

    public RodAssemblyBlockItem(Block block, Item.Properties props) {
        super(block, props);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryLayerPlace(ctx);
        return initialResult;
    }

    private void tryLayerPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) return;
        if (player.isShiftKeyDown()) return;

        Direction face = ctx.getClickedFace();
        // Only trigger when clicking the top face of the reactor (placing above it)
        if (face != Direction.UP) return;

        Level world      = ctx.getLevel();
        BlockPos pos     = ctx.getClickedPos();
        BlockPos onPos   = pos.relative(Direction.DOWN);
        BlockState onState = world.getBlockState(onPos);

        if (!ReactorCasingBlock.isReactor(onState)) return;

        // Resolve the multiblock controller so we know width and height.
        ReactorCasingBlockEntity reactorAt = ConnectivityHandler.partAt(
                AtomicBlockEntities.REACTOR_CASING.get(), world, onPos);
        if (reactorAt == null) return;
        ReactorCasingBlockEntity controller = reactorAt.getControllerBE();
        if (controller == null) return;

        int width = controller.getWidth();
        if (width <= 1) return; // 1x1: first placement already covered the only slot

        // The top layer of rod-assembly slots starts at controller.pos + (0, height, 0)
        BlockPos startPos = controller.getBlockPos().above(controller.getHeight());
        if (startPos.getY() != pos.getY()) return; // clicked somewhere other than the top

        // Pre-scan: count how many new blocks we need, and abort if any non-rod-assembly
        // block is in the way.
        ItemStack stack = ctx.getItemInHand();
        int needed = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                BlockPos slot = startPos.offset(x, 0, z);
                BlockState at = world.getBlockState(slot);
                if (at.getBlock() instanceof RodAssemblyBlock) continue; // already filled
                if (!at.canBeReplaced()) return; // something else is blocking
                needed++;
            }
        }

        if (!player.isCreative() && stack.getCount() < needed) return;

        // Place rod assemblies into every empty slot in the layer.
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                BlockPos slot = startPos.offset(x, 0, z);
                BlockState at = world.getBlockState(slot);
                if (at.getBlock() instanceof RodAssemblyBlock) continue;
                player.getPersistentData().putBoolean("SilenceTankSound", true);
                super.place(BlockPlaceContext.at(ctx, slot, Direction.UP));
                player.getPersistentData().remove("SilenceTankSound");
            }
        }
    }
}
