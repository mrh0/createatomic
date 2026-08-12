package com.mrh0.createatomic.index;

import com.mrh0.createatomic.items.FuelRodItem;
import com.mrh0.createatomic.items.HazmatArmorItem;
import com.mrh0.createatomic.items.PortableRTGItem;
import com.mrh0.createatomic.items.RodItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.mrh0.createatomic.CreateAtomic;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class AtomicItems {
    static {
        CreateAtomic.REGISTRATE.setCreativeTab(CreateAtomic.MAIN_TAB);
    }

    public static final ItemEntry<Item> RAW_URANIUM =
            CreateAtomic.REGISTRATE.item("raw_uranium", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> REFINED_URANIUM_NUGGET =
            CreateAtomic.REGISTRATE.item("refined_uranium_nugget", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> REFINED_URANIUM_INGOT =
            CreateAtomic.REGISTRATE.item("refined_uranium_ingot", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> URANIUM_INGOT =
            CreateAtomic.REGISTRATE.item("uranium_ingot", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> URANIUM_NUGGET =
            CreateAtomic.REGISTRATE.item("uranium_nugget", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<RodItem> SMALL_CONTROL_ROD =
            CreateAtomic.REGISTRATE.item("small_control_rod", RodItem::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<RodItem> LARGE_CONTROL_ROD =
            CreateAtomic.REGISTRATE.item("large_control_rod", RodItem::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<FuelRodItem> FUEL_ROD =
            CreateAtomic.REGISTRATE.item("refined_uranium_rod", FuelRodItem::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> DEPLETED_FUEL_ROD =
            CreateAtomic.REGISTRATE.item("depleted_uranium_rod", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> DENSE_ALLOY =
            CreateAtomic.REGISTRATE.item("dense_alloy", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> DENSE_ALLOY_NUGGET =
            CreateAtomic.REGISTRATE.item("dense_alloy_nugget", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    /*public static final ItemEntry<Item> DENSE_ALLOY =
            CreateAtomic.REGISTRATE.item("dense_alloy", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> GRAPHITE_DUST =
            CreateAtomic.REGISTRATE.item("graphite_dust", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> GRAPHITE_SHEET =
            CreateAtomic.REGISTRATE.item("graphite_sheet", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();*/

    public static final ItemEntry<FuelRodItem> PLUTONIUM_FUEL_ROD =
            CreateAtomic.REGISTRATE.item("plutonium_rod", FuelRodItem::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> DEPLETED_PLUTONIUM_FUEL_ROD =
            CreateAtomic.REGISTRATE.item("depleted_plutonium_rod", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<RodItem> NEUTRON_REFLECTOR =
            CreateAtomic.REGISTRATE.item("neutron_reflector", RodItem::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> PLUTONIUM_INGOT =
            CreateAtomic.REGISTRATE.item("plutonium_ingot", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> PLUTONIUM_NUGGET =
            CreateAtomic.REGISTRATE.item("plutonium_nugget", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<HazmatArmorItem> HAZMAT_HELMET =
            CreateAtomic.REGISTRATE.item("hazmat_helmet",
                    props -> new HazmatArmorItem(AtomicArmorMaterials.HAZMAT_SUIT, ArmorItem.Type.HELMET, props))
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<HazmatArmorItem> HAZMAT_CHESTPLATE =
            CreateAtomic.REGISTRATE.item("hazmat_chestplate",
                    props -> new HazmatArmorItem(AtomicArmorMaterials.HAZMAT_SUIT, ArmorItem.Type.CHESTPLATE, props))
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<HazmatArmorItem> HAZMAT_LEGGINGS =
            CreateAtomic.REGISTRATE.item("hazmat_leggings",
                    props -> new HazmatArmorItem(AtomicArmorMaterials.HAZMAT_SUIT, ArmorItem.Type.LEGGINGS, props))
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<HazmatArmorItem> HAZMAT_BOOTS =
            CreateAtomic.REGISTRATE.item("hazmat_boots",
                    props -> new HazmatArmorItem(AtomicArmorMaterials.HAZMAT_SUIT, ArmorItem.Type.BOOTS, props))
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> HEATSINK =
            CreateAtomic.REGISTRATE.item("heatsink", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<PortableRTGItem> PORTABLE_RTG =
            CreateAtomic.REGISTRATE.item("portable_rtg", PortableRTGItem::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static void register() {}
}
