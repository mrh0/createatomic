package com.mrh0.createatomic.client;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.entity.NuclearBombEntity;
import com.mrh0.createatomic.entity.NuclearBombRenderer;
import com.mrh0.createatomic.index.AtomicEntities;
import com.mrh0.createatomic.index.AtomicParticleTypes;
import com.mrh0.createatomic.particles.MushroomCloudParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = CreateAtomic.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AtomicClientSetup {

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AtomicParticleTypes.MUSHROOM_CLOUD.get(), MushroomCloudParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AtomicEntities.NUCLEAR_BOMB.get(), NuclearBombRenderer::new);
    }
}
