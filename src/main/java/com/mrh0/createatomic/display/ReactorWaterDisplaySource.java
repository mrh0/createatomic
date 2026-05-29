package com.mrh0.createatomic.display;

import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReactorWaterDisplaySource extends SingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity be = context.getSourceBlockEntity();
        if (!(be instanceof ReactorCasingBlockEntity rbe)) return EMPTY_LINE;
        ReactorCasingBlockEntity con = rbe.getControllerBE();
        if (con == null) return EMPTY_LINE;

        int amount = con.getTankInventory().getFluidAmount();
        int capacity = con.getTankInventory().getCapacity();
        boolean dry = con.isActive() && amount == 0;
        ChatFormatting color = dry ? ChatFormatting.RED : ChatFormatting.AQUA;
        return Component.literal(amount + " / " + capacity + " mB").withStyle(color);
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
