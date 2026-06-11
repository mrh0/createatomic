package com.mrh0.createatomic.index;

import com.mrh0.createatomic.CreateAtomic;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class AtomicArmorMaterials {

    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, CreateAtomic.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HAZMAT_SUIT =
            ARMOR_MATERIALS.register("hazmat_suit", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), m -> {
                        m.put(ArmorItem.Type.BOOTS, 2);
                        m.put(ArmorItem.Type.LEGGINGS, 4);
                        m.put(ArmorItem.Type.CHESTPLATE, 5);
                        m.put(ArmorItem.Type.HELMET, 2);
                    }),
                    5,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(AtomicItems.DENSE_ALLOY.get()),
                    List.of(new ArmorMaterial.Layer(CreateAtomic.asResource("hazmat"))),
                    0f,
                    0f
            ));

    public static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
    }
}
