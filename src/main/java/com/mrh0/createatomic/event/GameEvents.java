package com.mrh0.createatomic.event;

import com.mrh0.createatomic.blocks.reactor_casing.ReactorSoundManager;
import com.mrh0.createatomic.debug.AtomicDebugger;
import com.mrh0.createatomic.network.ObservePacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber
public class GameEvents {
    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Post evt) {
        //if (evt.phase == Phase.START) return;
        ObservePacketPayload.tick();
        AtomicDebugger.tick();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut evt) {
        ReactorSoundManager.invalidateAll();
    }
}
