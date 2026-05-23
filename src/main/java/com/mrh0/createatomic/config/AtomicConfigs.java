package com.mrh0.createatomic.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class AtomicConfigs {

    private static CAtomicServer server;

    public static CAtomicServer server() {
        return server;
    }

    public static void register(ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        server = new CAtomicServer();
        server.registerAll(builder);
        container.registerConfig(ModConfig.Type.SERVER, builder.build());
    }
}
