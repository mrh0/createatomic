package com.mrh0.createatomic.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class MushroomCloudParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    MushroomCloudParticle(ClientLevel level, double x, double y, double z,
                          double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;
        this.xd = dx;
        this.yd = dy + 0.015 + level.random.nextDouble() * 0.01;
        this.zd = dz;
        this.lifetime = 220 + level.random.nextInt(160);
        this.quadSize = 14.0f + level.random.nextFloat() * 14.0f;
        this.alpha = 0.8f;
        this.hasPhysics = false;
        this.gravity = 0;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.setSpriteFromAge(this.sprites);

        // Slowly expand
        this.quadSize *= 1.0018f;

        // Decelerate horizontal drift
        this.xd *= 0.97;
        this.zd *= 0.97;
        // Decelerate vertical rise
        this.yd *= 0.98;

        this.move(this.xd, this.yd, this.zd);

        // Fade out over last 80 ticks
        if (this.age > this.lifetime - 80) {
            this.alpha = Math.max(0f, this.alpha - (0.8f / 80f));
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new MushroomCloudParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
