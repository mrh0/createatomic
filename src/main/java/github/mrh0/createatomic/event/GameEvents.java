package github.mrh0.createatomic.event;

import github.mrh0.createatomic.debug.AtomicDebugger;
import github.mrh0.createatomic.network.ObservePacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber
public class GameEvents {
    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Post evt) {
        //if (evt.phase == Phase.START) return;
        ObservePacketPayload.tick();
        AtomicDebugger.tick();
    }
}
