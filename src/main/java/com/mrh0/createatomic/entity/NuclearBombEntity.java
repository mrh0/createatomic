package com.mrh0.createatomic.entity;

import com.mrh0.createatomic.Utility;
import com.mrh0.createatomic.index.AtomicParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.Nullable;

public class NuclearBombEntity extends Entity implements TraceableEntity {

    public static final int FUSE_TIME = 200;
    public static final float EXPLOSION_RADIUS = 40.0f;

    private static final EntityDataAccessor<Integer> DATA_FUSE =
            SynchedEntityData.defineId(NuclearBombEntity.class, EntityDataSerializers.INT);

    @Nullable
    private LivingEntity owner;

    public NuclearBombEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public NuclearBombEntity(EntityType<?> type, Level level, double x, double y, double z,
                             @Nullable LivingEntity owner) {
        this(type, level);
        this.setPos(x, y, z);
        this.owner = owner;
        this.setFuse(FUSE_TIME);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE, FUSE_TIME);
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);

        if (fuse <= 0) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide) {
                double rx = (this.level().random.nextDouble() - 0.5) * 0.3;
                double rz = (this.level().random.nextDouble() - 0.5) * 0.3;
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX() + rx, this.getY() + 0.5, this.getZ() + rz, 0, 0.01, 0);
                if (fuse % 3 == 0) {
                    this.level().addParticle(ParticleTypes.SMALL_FLAME,
                            this.getX() + rx, this.getY() + 0.4, this.getZ() + rz,
                            rx * 0.1, 0.02, rz * 0.1);
                }
            }
        }
    }

    private void explode() {
        Level level = this.level();
        level.explode(this, this.getX(), this.getY(), this.getZ(),
                EXPLOSION_RADIUS, true, ExplosionInteraction.TNT);
        Utility.applyRadiationInRadius(level, this.blockPosition(), (int) (EXPLOSION_RADIUS * 3), 2);
        if (level instanceof ServerLevel serverLevel) {
            spawnMushroomCloud(serverLevel);
        }
    }

    private void spawnMushroomCloud(ServerLevel level) {
        double cx = this.getX();
        double cy = this.getY();
        double cz = this.getZ();

        // Scattered TNT-style explosion puffs across the blast radius
        for (int i = 0; i < 48; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double dist = level.random.nextDouble() * EXPLOSION_RADIUS * 0.6;
            double height = level.random.nextDouble() * 8;
            level.sendParticles(ParticleTypes.EXPLOSION,
                    cx + Math.cos(angle) * dist, cy + height, cz + Math.sin(angle) * dist,
                    1, 0, 0, 0, 0);
        }

        // Ground-level shockwave ring
        for (int i = 0; i < 64; i++) {
            double angle = (i / 64.0) * Math.PI * 2;
            double dist = 4 + level.random.nextDouble() * 10;
            level.sendParticles(AtomicParticleTypes.MUSHROOM_CLOUD.get(),
                    cx + Math.cos(angle) * dist, cy + 0.5, cz + Math.sin(angle) * dist,
                    0, 0.0, 0.01, 0.0, 0);
        }

        // Stem - rising column
        for (int i = 0; i < 50; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double dist = level.random.nextDouble() * 3;
            double height = i * 0.65;
            level.sendParticles(AtomicParticleTypes.MUSHROOM_CLOUD.get(),
                    cx + Math.cos(angle) * dist, cy + height, cz + Math.sin(angle) * dist,
                    0, 0.0, 0.07, 0.0, 0);
        }

        // Cap underside - spreading outward at mid-height
        for (int i = 0; i < 80; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double dist = level.random.nextDouble() * 18;
            double height = 20 + level.random.nextDouble() * 6;
            level.sendParticles(AtomicParticleTypes.MUSHROOM_CLOUD.get(),
                    cx + Math.cos(angle) * dist, cy + height, cz + Math.sin(angle) * dist,
                    0, Math.cos(angle) * 0.03, 0.01, Math.sin(angle) * 0.03, 0);
        }

        // Cap top - wide toroidal ring
        for (int i = 0; i < 80; i++) {
            double angle = (i / 80.0) * Math.PI * 2;
            double dist = 8 + level.random.nextDouble() * 14;
            double height = 25 + level.random.nextDouble() * 6;
            level.sendParticles(AtomicParticleTypes.MUSHROOM_CLOUD.get(),
                    cx + Math.cos(angle) * dist, cy + height, cz + Math.sin(angle) * dist,
                    0, 0.0, 0.008, 0.0, 0);
        }

        // Anvil cap - flat cloud spreading at the very top
        for (int i = 0; i < 60; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double dist = level.random.nextDouble() * 25;
            double height = 30 + level.random.nextDouble() * 4;
            level.sendParticles(AtomicParticleTypes.MUSHROOM_CLOUD.get(),
                    cx + Math.cos(angle) * dist, cy + height, cz + Math.sin(angle) * dist,
                    0, Math.cos(angle) * 0.02, 0.003, Math.sin(angle) * 0.02, 0);
        }
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE);
    }

    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE, fuse);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setFuse(tag.getShort("Fuse"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("Fuse", (short) this.getFuse());
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }
}
