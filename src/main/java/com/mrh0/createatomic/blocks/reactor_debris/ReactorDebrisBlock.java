package com.mrh0.createatomic.blocks.reactor_debris;

import com.mrh0.createatomic.Utility;
import com.mrh0.createatomic.config.AtomicConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorDebrisBlock extends Block {

    private static final int RADIUS = 16;

    public ReactorDebrisBlock(Properties props) {
        super(props);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!AtomicConfigs.server().blockRadiationEnabled.get()) return;
        Utility.applyRadiationInRadius(level, pos, RADIUS, 0);
    }
}
