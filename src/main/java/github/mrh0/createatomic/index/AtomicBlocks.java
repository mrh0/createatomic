package github.mrh0.createatomic.index;

import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlock;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlock;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockItem;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingCTBehaviour;
import github.mrh0.createatomic.blocks.reactor_debris.ReactorDebrisBlock;
import github.mrh0.createatomic.groups.CreateAtomicGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class AtomicBlocks {
    private static final CreateRegistrate REGISTRATE = CreateAtomic.registrate()
            .creativeModeTab(() -> CreateAtomicGroup.MAIN);

    public static final BlockEntry<Block> RAW_URANIUM_BLOCK = REGISTRATE.block("raw_uranium_block",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GREEN).lightLevel((state) -> 2))
            .item(BlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> URANIUM_ORE = REGISTRATE.block("uranium_ore",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GREEN).lightLevel((state) -> 2))
            .item(BlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> DEEPSLATE_URANIUM_ORE = REGISTRATE.block("deepslate_uranium_ore",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GREEN).lightLevel((state) -> 2))
            .item(BlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> RADIOISOTOPE_HEAT_GENERATOR = REGISTRATE.block("radioisotope_heat_generator",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .item(BlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ReactorCasingBlock> REACTOR_CASING = REGISTRATE.block("reactor_casing",  ReactorCasingBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .onRegister(connectedTextures(ReactorCasingCTBehaviour::new))
            .item(ReactorCasingBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RodAssemblyBlock> ROD_ASSEMBLY = REGISTRATE.block("rod_assembly",  RodAssemblyBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .item(BlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ReactorDebrisBlock> REACTOR_DEBRIS = REGISTRATE.block("reactor_debris",  ReactorDebrisBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .item(BlockItem::new)
            .transform(customItemModel())
            .register();

    public static void register() {
        REGISTRATE.addToSection(RAW_URANIUM_BLOCK, AllSections.MATERIALS);
        REGISTRATE.addToSection(URANIUM_ORE, AllSections.MATERIALS);
        REGISTRATE.addToSection(DEEPSLATE_URANIUM_ORE, AllSections.MATERIALS);
    }
}
