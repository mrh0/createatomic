package com.mrh0.createatomic.blocks.reactor_casing;

import com.mrh0.createatomic.index.AtomicBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorCasingBlockItem extends BlockItem {

	public ReactorCasingBlockItem(Block block, Properties props) {
		super(block, props);
	}

	@Override
	public InteractionResult place(BlockPlaceContext ctx) {
		InteractionResult initialResult = super.place(ctx);
		if (!initialResult.consumesAction()) return initialResult;
		tryMultiPlace(ctx);
		return initialResult;
	}

	private void tryMultiPlace(BlockPlaceContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null) return;
		if (player.isShiftKeyDown()) return;
		Direction face = ctx.getClickedFace();
		if (!face.getAxis().isVertical()) return;
		ItemStack stack = ctx.getItemInHand();
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockPos placedOnPos = pos.relative(face.getOpposite());
		BlockState placedOnState = world.getBlockState(placedOnPos);

		if (!ReactorCasingBlock.isReactor(placedOnState)) return;
		ReactorCasingBlockEntity reactorAt = AtomicConnectivityHandler.partAt(AtomicBlockEntities.REACTOR_CASING.get(), world, placedOnPos);
		if (reactorAt == null) return;
		ReactorCasingBlockEntity controllerBE = reactorAt.getControllerBE();
		if (controllerBE == null) return;

		int width = controllerBE.width;
		if (width == 1) return;

		int blocksToPlace = 0;
		BlockPos startPos = face == Direction.DOWN ? controllerBE.getBlockPos()
			.below()
			: controllerBE.getBlockPos()
				.above(controllerBE.height);

		if (startPos.getY() != pos.getY()) return;

		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int zOffset = 0; zOffset < width; zOffset++) {
				BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
				BlockState blockState = world.getBlockState(offsetPos);
				if (ReactorCasingBlock.isReactor(blockState)) continue;
				if (!blockState.canBeReplaced()) return;
				blocksToPlace++;
			}
		}

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