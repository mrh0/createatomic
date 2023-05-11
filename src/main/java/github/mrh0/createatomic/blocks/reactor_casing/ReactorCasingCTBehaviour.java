package github.mrh0.createatomic.blocks.reactor_casing;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import github.mrh0.createatomic.index.AtomicSpriteShifts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorCasingCTBehaviour extends HorizontalCTBehaviour {

	public ReactorCasingCTBehaviour() {
		super(AtomicSpriteShifts.REACTOR_CASING, AtomicSpriteShifts.REACTOR_CASING_TOP);
	}

	@Override
	public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
		BlockPos otherPos, Direction face) {
		return state.getBlock() == other.getBlock() && ConnectivityHandler.isConnected(reader, pos, otherPos);
	}
}