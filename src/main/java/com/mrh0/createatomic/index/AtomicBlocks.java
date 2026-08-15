package com.mrh0.createatomic.index;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.blocks.nuclear_bomb.NuclearBombBlock;
import com.mrh0.createatomic.blocks.radioisotope_heat_generator.RadioisotopeHeatGeneratorBlock;
import com.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlock;
import com.mrh0.createatomic.blocks.rtg.RTGBlock;
import com.mrh0.createatomic.blocks.rtg.RTGBlockItem;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlock;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockItem;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingCTBehaviour;
import com.mrh0.createatomic.blocks.reactor_debris.ReactorDebrisBlock;
import com.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockItem;
import com.mrh0.createatomic.blocks.reactor_redstone_interface.ReactorRedstoneInterfaceBlock;
import com.mrh0.createatomic.blocks.turbine.TurbineBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;

public class AtomicBlocks {
    static {
        CreateAtomic.REGISTRATE.setCreativeTab(CreateAtomic.MAIN_TAB);
    }

    // Reactor Blocks
    public static final BlockEntry<ReactorCasingBlock> REACTOR_CASING = CreateAtomic.REGISTRATE.block("reactor_casing", ReactorCasingBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY))
            .onRegister(connectedTextures(ReactorCasingCTBehaviour::new))
            .blockstate((ctx, prov) -> {})
            .item(ReactorCasingBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RodAssemblyBlock> ROD_ASSEMBLY = CreateAtomic.REGISTRATE.block("rod_assembly", RodAssemblyBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY).randomTicks())
            .blockstate((ctx, prov) -> {})
            .item(RodAssemblyBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ReactorRedstoneInterfaceBlock> REACTOR_REDSTONE_INTERFACE =
                CreateAtomic.REGISTRATE.block("reactor_redstone_interface", ReactorRedstoneInterfaceBlock::new)
                        .initialProperties(SharedProperties::softMetal)
                        .properties(p -> p.mapColor(DyeColor.RED).strength(3.5f).requiresCorrectToolForDrops())
                        .blockstate((ctx, prov) -> {})
                        .item()
                        .transform(customItemModel())
                        .register();
        
    public static final BlockEntry<TurbineBlock> TURBINE = CreateAtomic.REGISTRATE.block("steam_turbine", TurbineBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY).strength(3.5f).requiresCorrectToolForDrops())
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RadioisotopeHeatGeneratorBlock> RADIOISOTOPE_HEAT_GENERATOR_INERT =
            CreateAtomic.REGISTRATE.block("radioisotope_heat_generator_none",
                    p -> new RadioisotopeHeatGeneratorBlock(p, HeatLevel.NONE, null))
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(DyeColor.GRAY))
                    .blockstate((ctx, prov) -> {})
                    .item()
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<RadioisotopeHeatGeneratorBlock> RADIOISOTOPE_HEAT_GENERATOR_SMOULDERING =
            CreateAtomic.REGISTRATE.block("radioisotope_heat_generator_smouldering",
                    p -> new RadioisotopeHeatGeneratorBlock(p, HeatLevel.SMOULDERING,
                            () -> RADIOISOTOPE_HEAT_GENERATOR_INERT.get()))
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(DyeColor.GRAY).randomTicks().lightLevel(s -> 5))
                    .blockstate((ctx, prov) -> {})
                    .item()
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<RadioisotopeHeatGeneratorBlock> RADIOISOTOPE_HEAT_GENERATOR_KINDLED =
            CreateAtomic.REGISTRATE.block("radioisotope_heat_generator_kindled",
                    p -> new RadioisotopeHeatGeneratorBlock(p, HeatLevel.KINDLED,
                            () -> RADIOISOTOPE_HEAT_GENERATOR_SMOULDERING.get()))
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(DyeColor.GRAY).randomTicks().lightLevel(s -> 13))
                    .blockstate((ctx, prov) -> {})
                    .item()
                    .transform(customItemModel())
                    .register();

    /*public static final BlockEntry<CakeBlock> YELLOW_CAKE = CreateAtomic.REGISTRATE.block("yellow_cake", CakeBlock::new)
            .properties(p -> p.noOcclusion().strength(0.5f).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY))
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();*/

    public static final BlockEntry<NuclearBombBlock> CRUDE_NUCLEAR_BOMB = CreateAtomic.REGISTRATE.block("crude_nuclear_bomb", NuclearBombBlock::new)
            .initialProperties(() -> Blocks.TNT)
            .properties(p -> p.mapColor(DyeColor.YELLOW))
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RTGBlock> RTG = CreateAtomic.REGISTRATE.block("rtg", RTGBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY).lightLevel(s -> 3).requiresCorrectToolForDrops().noOcclusion())
            .blockstate((ctx, prov) -> {})
            .item(RTGBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ReactorDebrisBlock> REACTOR_DEBRIS = CreateAtomic.REGISTRATE.block("reactor_debris", ReactorDebrisBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY).randomTicks().strength(400f, 1200f).requiresCorrectToolForDrops())
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    // Storage Blocks
    public static final BlockEntry<Block> PLUTONIUM_BLOCK = CreateAtomic.REGISTRATE.block("plutonium_block", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.PURPLE).requiresCorrectToolForDrops())
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> URANIUM_BLOCK = CreateAtomic.REGISTRATE.block("uranium_block", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GREEN).requiresCorrectToolForDrops())
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> REFINED_URANIUM_BLOCK = CreateAtomic.REGISTRATE.block("refined_uranium_block", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.LIME).requiresCorrectToolForDrops())
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> DENSE_ALLOY_BLOCK = CreateAtomic.REGISTRATE.block("dense_alloy_block", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GRAY).requiresCorrectToolForDrops())
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    // Ore Blocks
    public static final BlockEntry<Block> RAW_URANIUM_BLOCK = CreateAtomic.REGISTRATE.block("raw_uranium_block", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GREEN).lightLevel((state) -> 2))
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> URANIUM_ORE = CreateAtomic.REGISTRATE.block("uranium_ore", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GREEN).lightLevel((state) -> 2))
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> DEEPSLATE_URANIUM_ORE = CreateAtomic.REGISTRATE.block("deepslate_uranium_ore", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(DyeColor.GREEN).lightLevel((state) -> 2))
            .blockstate((ctx, prov) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {}
}
