package github.mrh0.createatomic.index;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import github.mrh0.createatomic.CreateAtomic;
import github.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;

public class AtomicBlockEntities {
    public static final BlockEntityEntry<ReactorCasingBlockEntity> REACTOR_CASING = CreateAtomic.registrate()
            .tileEntity("reactor_casing", ReactorCasingBlockEntity::new)
            .validBlocks(AtomicBlocks.REACTOR_CASING)
            //.renderer(() -> ModularAccumulatorRenderer::new)
            .register();

    public static void register() {}
}
