package com.mrh0.createatomic.index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mrh0.createatomic.CreateAtomic;
import net.minecraft.resources.ResourceLocation;

public class AtomicPartials {
    public static final PartialModel REACTOR_GUAGE = block("reactor/guage");
    public static final PartialModel REACTOR_DIAL = block("reactor/dial");
    public static final PartialModel TURBINE_BLADE = block("turbine/blades");

    public static final PartialModel ROD_FUEL              = block("rod_assembly/rod_fuel");
    public static final PartialModel ROD_DEPLETED          = block("rod_assembly/rod_fuel_depleted");
    public static final PartialModel ROD_PLUTONIUM_FUEL    = block("rod_assembly/rod_plutonium_fuel");
    public static final PartialModel ROD_DEPLETED_PLUTONIUM = block("rod_assembly/rod_plutonium_fuel_depleted");
    public static final PartialModel ROD_CONTROL_SMALL     = block("rod_assembly/rod_control_small");
    public static final PartialModel ROD_CONTROL_LARGE     = block("rod_assembly/rod_control_large");
    public static final PartialModel ROD_REFLECTOR         = block("rod_assembly/rod_reflector");
    public static final PartialModel ROD_COOLING           = block("rod_assembly/rod_cooling");
    public static final PartialModel ROD_PLATE             = block("rod_assembly/rod_plate");

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, "block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, "entity/" + path));
    }

    public static void init() {}
}
