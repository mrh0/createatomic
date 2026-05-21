package github.mrh0.createatomic.index;

import github.mrh0.createatomic.CreateAtomic;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AtomicSounds {

    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, CreateAtomic.MODID);

    public static final Holder<SoundEvent> REACTOR_BEEP = SOUNDS.register("reactor_beep",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, "reactor_beep")));

    public static final Holder<SoundEvent> REACTOR_LOOP = SOUNDS.register("reactor_loop",
            () -> SoundEvent.createFixedRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, "reactor_loop"), 32f));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
