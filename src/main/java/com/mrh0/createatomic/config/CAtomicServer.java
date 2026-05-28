package com.mrh0.createatomic.config;

import net.createmod.catnip.config.ConfigBase;

public class CAtomicServer extends ConfigBase {

    public final ConfigGroup reactor = group(0, "reactor", "Reactor settings");

    public final ConfigInt fuelRodDuration = i(72000, 1, Integer.MAX_VALUE, "fuelRodDuration",
            "How many ticks a uranium fuel rod lasts before becoming depleted.",
            "72000 = 60 minutes at 20 TPS.");

    public final ConfigBool meltdownEnabled = b(true, "meltdownEnabled",
            "Whether reactors can melt down. When false, a reactor at 0% hull integrity",
            "shuts down instead of exploding, and slowly self-repairs when cooled.");

    public final ConfigBool meltdownExplosion = b(true, "meltdownExplosion",
            "Whether a reactor meltdown triggers an explosion.");

    public final ConfigBool debrisRadiation = b(true, "debrisRadiation",
            "Whether reactor debris emits radiation sickness effects to nearby entities.");

    public final ConfigFloat hullRegenRate = f(0.5f, 0f, 100f, "hullRegenRate",
            "Hull integrity regenerated per lazy tick (every ~1 s) when the reactor is fully cooled.",
            "0.5 = full regen from 0% in ~160 seconds.");

    public final ConfigFloat rhgDecayDays = f(5f, 0.01f, 3650.0f, "rhgDecayDays",
            "Average real-world days (24 hours at 20 TPS) between each decay step of the Radioisotope Heat Generator.",
            "Each step: heated -> smouldering -> inert.",
            "1.0 = on average 24 real hours (~1,728,000 ticks) per step.");

    @Override
    public String getName() {
        return "server";
    }
}
