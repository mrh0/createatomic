package github.mrh0.createatomic.index;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;

public class AtomicBlockEntities {
    public static final BlockEntityEntry<ReactorCasingBlockEntity> REACTOR_CASING = CreateAtomic.REGISTRATE
            .blockEntity("reactor_casing", ReactorCasingBlockEntity::new)
            .validBlocks(AtomicBlocks.REACTOR_CASING)
            //.renderer(() -> ModularAccumulatorRenderer::new)
            .register();

    public static final BlockEntityEntry<RodAssemblyBlockEntity> ROD_ASSEMBLY = CreateAtomic.REGISTRATE
            .blockEntity("rod_assembly", RodAssemblyBlockEntity::new)
            .validBlocks(AtomicBlocks.ROD_ASSEMBLY)
            //.renderer(() -> ModularAccumulatorRenderer::new)
            .register();

    public static void register() {}
}
