package com.mrh0.createatomic.blocks.rod_assembly;

import net.minecraft.util.StringRepresentable;

public enum RodInsertionBehaviour implements StringRepresentable {
    SCRAM, // Inserted when active and not armed (i.e. control rods)
    ARMED, // Inserted when armed (i.e. neutron reflectors)
    ACTIVE, // Inserted when active (i.e. fuel rods)
    NEVER, // Never inserted (i.e. depleted fuel rods)
    ALWAYS; // Always inserted (for testing purposes)

    public static boolean shouldInsert(RodInsertionBehaviour behaviour, boolean active, boolean armed) {
        return switch (behaviour) {
            case SCRAM -> active && !armed;
            case ARMED -> armed;
            case ACTIVE -> active;
            case NEVER -> false;
            case ALWAYS -> true;
        };
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case SCRAM -> "scram";
            case ARMED -> "armed";
            case ACTIVE -> "active";
            case NEVER -> "never";
            case ALWAYS -> "always";
        };
    }
}
