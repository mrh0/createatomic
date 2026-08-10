package com.mrh0.createatomic.index;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;
import com.mrh0.createatomic.blocks.rtg.RTGBlockEntity;
import com.mrh0.createatomic.blocks.turbine.TurbineBlockEntity;

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

    public static final BlockEntityEntry<TurbineBlockEntity> TURBINE = CreateAtomic.REGISTRATE
            .blockEntity("steam_turbine", TurbineBlockEntity::new)
            .validBlocks(AtomicBlocks.TURBINE)
            .register();

    public static final BlockEntityEntry<RTGBlockEntity> RTG = CreateAtomic.REGISTRATE
            .blockEntity("rtg", RTGBlockEntity::new)
            .validBlocks(AtomicBlocks.RTG)
            .register();

    public static void register() {}
}
