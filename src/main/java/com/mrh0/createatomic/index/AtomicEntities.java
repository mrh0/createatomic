package com.mrh0.createatomic.index;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.entity.NuclearBombEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AtomicEntities {

    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CreateAtomic.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<NuclearBombEntity>> NUCLEAR_BOMB =
            ENTITIES.register("crude_nuclear_bomb", () ->
                    EntityType.Builder.<NuclearBombEntity>of(NuclearBombEntity::new, MobCategory.MISC)
                            .sized(0.98F, 0.98F)
                            .clientTrackingRange(8)
                            .updateInterval(10)
                            .build("createatomic:crude_nuclear_bomb"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
