package com.mrh0.createatomic.blocks.reactor_casing;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-only. Manages one looping sound instance per active reactor controller.
 * Called from ReactorCasingBlockEntity.tick() gated behind level.isClientSide().
 */
public class ReactorSoundManager {

    private static final Map<BlockPos, ReactorLoopingSound> SOUNDS = new HashMap<>();

    public static void tick(ReactorCasingBlockEntity be) {
        BlockPos pos   = be.getBlockPos();
        boolean active = be.getTemperature() > 25;

        ReactorLoopingSound existing = SOUNDS.get(pos);

        if (active) {
            if (existing == null || !existing.isRunning()) {
                ReactorLoopingSound sound = new ReactorLoopingSound(be);
                Minecraft.getInstance().getSoundManager().play(sound);
                SOUNDS.put(pos, sound);
            }
        } else if (existing != null) {
            existing.requestStop();
            SOUNDS.remove(pos);
        }
    }

    public static void invalidate(BlockPos pos) {
        ReactorLoopingSound sound = SOUNDS.remove(pos);
        if (sound != null) sound.requestStop();
    }

    public static void invalidateAll() {
        SOUNDS.values().forEach(ReactorLoopingSound::requestStop);
        SOUNDS.clear();
    }
}
