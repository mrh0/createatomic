package com.mrh0.createatomic.blocks.turbine;

import net.minecraft.util.StringRepresentable;

public enum TurbineType implements StringRepresentable {
    INLET("inlet"),
    INLINE("inline"),
    OUTLET("outlet");

    private final String name;

    TurbineType(String name) { this.name = name; }

    @Override
    public String getSerializedName() { return name; }
}
