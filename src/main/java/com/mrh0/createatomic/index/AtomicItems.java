package com.mrh0.createatomic.index;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.mrh0.createatomic.CreateAtomic;
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

    public static final ItemEntry<Item> SMALL_CONTROL_ROD =
            CreateAtomic.REGISTRATE.item("small_control_rod", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> LARGE_CONTROL_ROD =
            CreateAtomic.REGISTRATE.item("large_control_rod", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> FUEL_ROD =
            CreateAtomic.REGISTRATE.item("refined_uranium_rod", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> DEPLETED_FUEL_ROD =
            CreateAtomic.REGISTRATE.item("depleted_uranium_rod", Item::new)
                    .model((ctx, prov) -> prov.generated(ctx))
                    .register();

    public static final ItemEntry<Item> HULL_PLATE =
            CreateAtomic.REGISTRATE.item("hull_plate", Item::new)
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

    public static final ItemEntry<Item> NEUTRON_REFLECTOR =
            CreateAtomic.REGISTRATE.item("neutron_reflector", Item::new)
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

    public static void register() {}
}
