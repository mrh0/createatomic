package github.mrh0.createatomic.blocks.turbine;

import net.minecraft.util.StringRepresentable;

public enum TurbineType implements StringRepresentable {
    /** First turbine in a chain — no same-facing turbine on the intake side. */
    INLET("inlet"),
    /** Middle turbine — same-facing turbines on both intake and output sides. */
    INLINE("inline"),
    /** Last turbine in a chain — same-facing turbine on intake side, nothing on output side. */
    OUTLET("outlet");

    private final String name;

    TurbineType(String name) { this.name = name; }

    @Override
    public String getSerializedName() { return name; }
}
