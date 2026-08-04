package com.mrh0.createatomic.datagen;

import com.mrh0.createatomic.index.AtomicBlocks;
import com.mrh0.createatomic.index.AtomicItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

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
        this.dropSelf(AtomicBlocks.PLUTONIUM_BLOCK.get());
        this.dropSelf(AtomicBlocks.URANIUM_BLOCK.get());
        this.dropSelf(AtomicBlocks.REFINED_URANIUM_BLOCK.get());
        this.dropSelf(AtomicBlocks.DENSE_ALLOY_BLOCK.get());
        this.dropSelf(AtomicBlocks.RAW_URANIUM_BLOCK.get());
        this.add(AtomicBlocks.REACTOR_DEBRIS.get(), block ->
                this.createSingleItemTable(AtomicItems.DENSE_ALLOY_NUGGET.get(), UniformGenerator.between(1, 3)));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
            AtomicBlocks.URANIUM_ORE.get(),
            AtomicBlocks.DEEPSLATE_URANIUM_ORE.get(),
            AtomicBlocks.PLUTONIUM_BLOCK.get(),
            AtomicBlocks.URANIUM_BLOCK.get(),
            AtomicBlocks.REFINED_URANIUM_BLOCK.get(),
            AtomicBlocks.DENSE_ALLOY_BLOCK.get(),
            AtomicBlocks.RAW_URANIUM_BLOCK.get(),
            AtomicBlocks.REACTOR_DEBRIS.get()
        );
    }
}
