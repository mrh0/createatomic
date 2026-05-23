package com.mrh0.createatomic.datagen.TagProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CAItemTagProvider extends ItemTagsProvider {
    public CAItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreateAtomic.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Mirror block tags onto their item equivalents
        copy(CATagRegister.Blocks.ORES, CATagRegister.Items.ORES);
        copy(CATagRegister.Blocks.ORES_URANIUM, CATagRegister.Items.ORES_URANIUM);
        copy(CATagRegister.Blocks.ORES_IN_GROUND_STONE, CATagRegister.Items.ORES_IN_GROUND_STONE);
        copy(CATagRegister.Blocks.ORES_IN_GROUND_DEEPSLATE, CATagRegister.Items.ORES_IN_GROUND_DEEPSLATE);
        copy(CATagRegister.Blocks.STORAGE_BLOCKS, CATagRegister.Items.STORAGE_BLOCKS);
        copy(CATagRegister.Blocks.STORAGE_BLOCKS_RAW_URANIUM, CATagRegister.Items.STORAGE_BLOCKS_RAW_URANIUM);

        // Raw materials
        tag(CATagRegister.Items.RAW_MATERIALS)
                .add(AtomicItems.RAW_URANIUM.get());
        tag(CATagRegister.Items.RAW_MATERIALS_URANIUM)
                .add(AtomicItems.RAW_URANIUM.get());

        // Ingots
        tag(CATagRegister.Items.INGOTS)
                .add(AtomicItems.REFINED_URANIUM.get())
                .add(AtomicItems.DEPLETED_URANIUM.get())
                .add(AtomicItems.PLUTONIUM_INGOT.get());
        tag(CATagRegister.Items.INGOTS_URANIUM)
                .add(AtomicItems.REFINED_URANIUM.get());
        tag(CATagRegister.Items.INGOTS_DEPLETED_URANIUM)
                .add(AtomicItems.DEPLETED_URANIUM.get());
        tag(CATagRegister.Items.INGOTS_PLUTONIUM)
                .add(AtomicItems.PLUTONIUM_INGOT.get());

        // Nuggets
        tag(CATagRegister.Items.NUGGETS)
                .add(AtomicItems.URANIUM_NUGGET.get())
                .add(AtomicItems.DEPLETED_URANIUM_NUGGET.get())
                .add(AtomicItems.PLUTONIUM_NUGGET.get());
        tag(CATagRegister.Items.NUGGETS_URANIUM)
                .add(AtomicItems.URANIUM_NUGGET.get());
        tag(CATagRegister.Items.NUGGETS_DEPLETED_URANIUM)
                .add(AtomicItems.DEPLETED_URANIUM_NUGGET.get());
        tag(CATagRegister.Items.NUGGETS_PLUTONIUM)
                .add(AtomicItems.PLUTONIUM_NUGGET.get());

        // Mod-specific reactor tags (createatomic: namespace)
        tag(CATagRegister.Items.CONTROL_RODS)
                .add(AtomicItems.SMALL_CONTROL_ROD.get())
                .add(AtomicItems.LARGE_CONTROL_ROD.get());

        tag(CATagRegister.Items.FUEL_RODS)
                .add(AtomicItems.FUEL_ROD.get())
                .add(AtomicItems.DEPLETED_FUEL_ROD.get());

        tag(CATagRegister.Items.REACTOR_COMPONENTS)
                .add(AtomicItems.SMALL_CONTROL_ROD.get())
                .add(AtomicItems.LARGE_CONTROL_ROD.get())
                .add(AtomicItems.NEUTRON_REFLECTOR.get())
                .add(AtomicItems.FUEL_ROD.get())
                .add(AtomicItems.DEPLETED_FUEL_ROD.get());
    }
}
