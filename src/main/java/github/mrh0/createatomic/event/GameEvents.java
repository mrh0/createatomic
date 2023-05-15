package github.mrh0.createatomic.event;

import github.mrh0.createatomic.debug.AtomicDebugger;
import github.mrh0.createatomic.network.ObservePacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GameEvents {
    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent evt) {
        if(evt.phase == TickEvent.Phase.START) return;
        ObservePacket.tick();
        AtomicDebugger.tick();
    }
}
