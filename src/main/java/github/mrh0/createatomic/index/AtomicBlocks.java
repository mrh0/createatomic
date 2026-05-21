package github.mrh0.createatomic.index;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlock;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlock;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockItem;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingCTBehaviour;
import github.mrh0.createatomic.blocks.reactor_debris.ReactorDebrisBlock;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockItem;
import github.mrh0.createatomic.blocks.reactor_redstone_interface.ReactorRedstoneInterfaceBlock;
import github.mrh0.createatomic.blocks.turbine.TurbineBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class AtomicBlocks {
    static {
        CreateAtomic.REGISTRATE.setCreativeTab(CreateAtomic.MAIN_TAB);
    }

    public static final BlockEntry<Block> RAW_URANIUM_BLOCK = CreateAtomic.REGISTRATE.block("raw_uranium_block",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GREEN).lightLevel((state) -> 2))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> URANIUM_ORE = CreateAtomic.REGISTRATE.block("uranium_ore",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GREEN).lightLevel((state) -> 2))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> DEEPSLATE_URANIUM_ORE = CreateAtomic.REGISTRATE.block("deepslate_uranium_ore",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GREEN).lightLevel((state) -> 2))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> RADIOISOTOPE_HEAT_GENERATOR = CreateAtomic.REGISTRATE.block("radioisotope_heat_generator",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ReactorCasingBlock> REACTOR_CASING = CreateAtomic.REGISTRATE.block("reactor_casing",  ReactorCasingBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY))
            .onRegister(connectedTextures(ReactorCasingCTBehaviour::new))
            .item(ReactorCasingBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RodAssemblyBlock> ROD_ASSEMBLY = CreateAtomic.REGISTRATE.block("rod_assembly",  RodAssemblyBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY))
            .item(RodAssemblyBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ReactorDebrisBlock> REACTOR_DEBRIS = CreateAtomic.REGISTRATE.block("reactor_debris",  ReactorDebrisBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY).randomTicks())
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ReactorRedstoneInterfaceBlock> REACTOR_REDSTONE_INTERFACE =
            CreateAtomic.REGISTRATE.block("reactor_redstone_interface", ReactorRedstoneInterfaceBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(DyeColor.RED).strength(3.5f).requiresCorrectToolForDrops())
                    .item()
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<TurbineBlock> TURBINE = CreateAtomic.REGISTRATE.block("steam_turbine", TurbineBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY).strength(3.5f).requiresCorrectToolForDrops())
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {}
}
