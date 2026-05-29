package com.mrh0.createatomic.display;

import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReactorStatusDisplaySource extends SingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity be = context.getSourceBlockEntity();
        if (!(be instanceof ReactorCasingBlockEntity rbe)) return EMPTY_LINE;
        ReactorCasingBlockEntity con = rbe.getControllerBE();
        if (con == null) return EMPTY_LINE;

        if (con.cachedScrammed)
            return Component.translatable("createatomic.display_source.reactor.scrammed")
                    .withStyle(ChatFormatting.RED);
        if (con.isActive())
            return Component.translatable("createatomic.display_source.reactor.active")
                    .withStyle(ChatFormatting.GREEN);
        return Component.translatable("createatomic.display_source.reactor.inactive")
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
