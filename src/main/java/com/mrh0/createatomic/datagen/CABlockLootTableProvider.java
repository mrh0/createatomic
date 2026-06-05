package com.mrh0.createatomic.datagen;

import com.mrh0.createatomic.index.AtomicBlocks;
import com.mrh0.createatomic.index.AtomicItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;

public class CABlockLootTableProvider extends BlockLootSubProvider {
    protected CABlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(AtomicBlocks.URANIUM_ORE.get(), block -> this.createOreDrop(block, AtomicItems.RAW_URANIUM.get()));
        this.add(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get(), block -> this.createOreDrop(block, AtomicItems.RAW_URANIUM.get()));
        this.dropSelf(AtomicBlocks.RAW_URANIUM_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
            AtomicBlocks.URANIUM_ORE.get(),
            AtomicBlocks.DEEPSLATE_URANIUM_ORE.get(),
            AtomicBlocks.RAW_URANIUM_BLOCK.get()
        );
    }
}
