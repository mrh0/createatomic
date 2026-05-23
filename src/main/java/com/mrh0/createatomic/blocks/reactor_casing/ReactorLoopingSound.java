package com.mrh0.createatomic.blocks.reactor_casing;

import com.mrh0.createatomic.index.AtomicSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ReactorLoopingSound extends AbstractTickableSoundInstance {

    private static final float MAX_DISTANCE = 24f;
    private static final float MAX_VOLUME   = 0.05f;

    private final ReactorCasingBlockEntity reactor;
    private boolean shouldStop = false;

    public ReactorLoopingSound(ReactorCasingBlockEntity reactor) {
        super(AtomicSounds.REACTOR_LOOP.value(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
        this.reactor     = reactor;
        this.looping     = true;
        this.delay       = 0;
        this.pitch       = 0.5f;
        this.attenuation = Attenuation.NONE; // we handle volume manually
    }

    private Vec3 getCenter() {
        return new Vec3(
            reactor.getBlockPos().getX() + reactor.width  / 2.0,
            reactor.getBlockPos().getY() + reactor.height / 2.0,
            reactor.getBlockPos().getZ() + reactor.width  / 2.0
        );
    }

    @Override public double getX() { return getCenter().x; }
    @Override public double getY() { return getCenter().y; }
    @Override public double getZ() { return getCenter().z; }

    @Override
    public float getVolume() {
        Entity camera = Minecraft.getInstance().cameraEntity;
        if (camera == null) return 0f;
        double dist = camera.position().distanceTo(getCenter());
        return Mth.clamp((float)(1.0 - dist / MAX_DISTANCE), 0f, 1f) * MAX_VOLUME;
    }

    public void requestStop() { shouldStop = true; }
    public boolean isRunning() { return !shouldStop; }

    @Override
    public void tick() {
        if (shouldStop || reactor.isRemoved()) {
            shouldStop = true;
            stop();
        }
    }

    @Override public boolean canPlaySound()  { return !reactor.isRemoved() && !shouldStop; }
    @Override public boolean canStartSilent() { return true; }
}
