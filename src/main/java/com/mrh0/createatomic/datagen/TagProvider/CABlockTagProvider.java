package com.mrh0.createatomic.datagen.TagProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CABlockTagProvider extends BlockTagsProvider {
    public CABlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateAtomic.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // All blocks are mineable with a pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(AtomicBlocks.RAW_URANIUM_BLOCK.get())
                .add(AtomicBlocks.URANIUM_ORE.get())
                .add(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get())
                .add(AtomicBlocks.RADIOISOTOPE_HEAT_GENERATOR_KINDLED.get())
                .add(AtomicBlocks.REACTOR_CASING.get())
                .add(AtomicBlocks.ROD_ASSEMBLY.get())
                .add(AtomicBlocks.REACTOR_DEBRIS.get())
                .add(AtomicBlocks.REACTOR_REDSTONE_INTERFACE.get())
                .add(AtomicBlocks.TURBINE.get());

        // Uranium ores and raw block require iron-tier tool to drop
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(AtomicBlocks.URANIUM_ORE.get())
                .add(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get())
                .add(AtomicBlocks.RAW_URANIUM_BLOCK.get());

        // Machine blocks require stone-tier tool
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(AtomicBlocks.RADIOISOTOPE_HEAT_GENERATOR_KINDLED.get())
                .add(AtomicBlocks.REACTOR_CASING.get())
                .add(AtomicBlocks.ROD_ASSEMBLY.get())
                .add(AtomicBlocks.REACTOR_DEBRIS.get())
                .add(AtomicBlocks.REACTOR_REDSTONE_INTERFACE.get())
                .add(AtomicBlocks.TURBINE.get());

        // Common ore tags (c: namespace)
        tag(CATagRegister.Blocks.ORES)
                .add(AtomicBlocks.URANIUM_ORE.get())
                .add(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get());

        tag(CATagRegister.Blocks.ORES_URANIUM)
                .add(AtomicBlocks.URANIUM_ORE.get())
                .add(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get());

        tag(CATagRegister.Blocks.ORES_IN_GROUND_STONE)
                .add(AtomicBlocks.URANIUM_ORE.get());

        tag(CATagRegister.Blocks.ORES_IN_GROUND_DEEPSLATE)
                .add(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get());

        // Storage blocks
        tag(CATagRegister.Blocks.STORAGE_BLOCKS)
                .add(AtomicBlocks.RAW_URANIUM_BLOCK.get());

        tag(CATagRegister.Blocks.STORAGE_BLOCKS_RAW_URANIUM)
                .add(AtomicBlocks.RAW_URANIUM_BLOCK.get());
    }
}
