package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicBlocks;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class AtomicCrushingRecipeGen extends CrushingRecipeGen {

    GeneratedRecipe URANIUM_ORE = create(CreateAtomic.MODID, AtomicBlocks.URANIUM_ORE::get, b -> b
        .duration(350)
        .output(AllItems.CRUSHED_URANIUM.get(), 2)
        .output(0.5f, AllItems.CRUSHED_URANIUM.get(), 1)
        .output(0.75f, AllItems.EXP_NUGGET.get(), 1)
        .output(0.125f, Blocks.COBBLESTONE)
    );

    GeneratedRecipe DEEPSLATE_URANIUM_ORE = create(CreateAtomic.MODID, AtomicBlocks.DEEPSLATE_URANIUM_ORE::get, b -> b
        .duration(500)
        .output(AllItems.CRUSHED_URANIUM.get(), 2)
        .output(0.75f, AllItems.CRUSHED_URANIUM.get(), 1)
        .output(0.75f, AllItems.EXP_NUGGET.get(), 1)
        .output(0.125f, Blocks.COBBLED_DEEPSLATE)
    );

    GeneratedRecipe FUEL_ROD_RECYCLE = create(CreateAtomic.MODID, AtomicItems.FUEL_ROD::get, b -> b
        .duration(350)
        .output(AtomicItems.REFINED_URANIUM_NUGGET.get(), 4)
        .output(AtomicItems.URANIUM_NUGGET.get(), 2)
        .output(AtomicItems.DENSE_ALLOY_NUGGET.get(), 5)
        .output(0.5f, AtomicItems.URANIUM_NUGGET.get(), 1)
    );

    GeneratedRecipe DEPLETED_FUEL_ROD_RECYCLE = create(CreateAtomic.MODID, AtomicItems.DEPLETED_FUEL_ROD::get, b -> b
        .duration(350)
        .output(AtomicItems.URANIUM_NUGGET.get(), 4)
        .output(AtomicItems.PLUTONIUM_NUGGET.get(), 2)
        .output(AtomicItems.DENSE_ALLOY_NUGGET.get(), 5)
        .output(0.5f, AtomicItems.PLUTONIUM_NUGGET.get(), 1)
    );

    public AtomicCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAtomic.MODID);
    }
}
