package com.mrh0.createatomic.datagen;

import com.mrh0.createatomic.CreateAtomic;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class AtomicProjectileTypesDatagen {
    public static void bootstrap(BootstrapContext<PotatoCannonProjectileType> bs) {
        /*register(bs, "yellow_cake", new PotatoCannonProjectileType.Builder()
                .damage(8)
                .addItems(CABlocks.CHOCOLATE_CAKE.asItem())
                .knockback(0.1f)
                .reloadTicks(15)
                .renderTumbling()
                .sticky()
                .velocity(1.1f)
                .soundPitch(0.8f)
                .build()
        );*/
    }

    private static void register(BootstrapContext<PotatoCannonProjectileType> bs, String name, PotatoCannonProjectileType type) {
        bs.register(ResourceKey.create(CreateRegistries.POTATO_PROJECTILE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, name)), type);
    }
}
