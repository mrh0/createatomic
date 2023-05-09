package github.mrh0.createatomic.index;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.groups.CreateAtomicGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.content.logistics.block.display.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class AtomicBlocks {
    private static final CreateRegistrate REGISTRATE = CreateAtomic.registrate()
            .creativeModeTab(() -> CreateAtomicGroup.MAIN);

    public static final BlockEntry<Block> RAW_URANIUM_BLOCK = REGISTRATE.block("raw_uranium_block",  Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.COLOR_GREEN))
            .item(BlockItem::new)
            .transform(customItemModel())
            .register();

    public static void register() {}
}
