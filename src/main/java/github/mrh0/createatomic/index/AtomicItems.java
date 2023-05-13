package github.mrh0.createatomic.index;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.groups.CreateAtomicGroup;
import net.minecraft.world.item.Item;

public class AtomicItems {
    private static final CreateRegistrate REGISTRATE = CreateAtomic.registrate()
            .creativeModeTab(() -> CreateAtomicGroup.MAIN);

    public static final ItemEntry<Item> RAW_URANIUM =
            REGISTRATE.item("raw_uranium", Item::new)
                    .register();

    public static final ItemEntry<Item> URANIUM_NUGGET =
            REGISTRATE.item("uranium_nugget", Item::new)
                    .register();
    public static final ItemEntry<Item> REFINED_URANIUM =
            REGISTRATE.item("refined_uranium", Item::new)
                    .register();

    public static final ItemEntry<Item> DEPLETED_URANIUM =
            REGISTRATE.item("depleted_uranium", Item::new)
                    .register();
    public static final ItemEntry<Item> DEPLETED_URANIUM_NUGGET =
            REGISTRATE.item("depleted_uranium_nugget", Item::new)
                    .register();

    public static final ItemEntry<Item> SMALL_CONTROL_ROD =
            REGISTRATE.item("small_control_rod", Item::new)
                    .register();
    public static final ItemEntry<Item> LARGE_CONTROL_ROD =
            REGISTRATE.item("large_control_rod", Item::new)
                    .register();

    public static final ItemEntry<Item> FUEL_ROD =
            REGISTRATE.item("refined_uranium_rod", Item::new)
                    .register();
    public static final ItemEntry<Item> DEPLETED_FUEL_ROD =
            REGISTRATE.item("depleted_uranium_rod", Item::new)
                    .register();

    public static void register() {}
}
