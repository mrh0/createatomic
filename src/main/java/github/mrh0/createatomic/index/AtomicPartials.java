package github.mrh0.createatomic.index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import github.mrh0.createatomic.CreateAtomic;
import net.minecraft.resources.ResourceLocation;

public class AtomicPartials {
    public static final PartialModel REACTOR_GUAGE = block("reactor/guage");
    public static final PartialModel REACTOR_DIAL = block("reactor/dial");
    public static final PartialModel TURBINE_BLADE = block("turbine/blades");

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, "block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, "entity/" + path));
    }

    public static void init() {
        // init static fields
    }
}
