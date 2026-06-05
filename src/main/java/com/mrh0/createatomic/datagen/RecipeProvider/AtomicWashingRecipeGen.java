package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AtomicWashingRecipeGen extends WashingRecipeGen {

    GeneratedRecipe CRUSHED_URANIUM = create(CreateAtomic.MODID, AllItems.CRUSHED_URANIUM::get, b -> b
        .output(AtomicItems.URANIUM_NUGGET.get(), 6)
        .output(AtomicItems.REFINED_URANIUM_NUGGET.get(), 3)
        .output(0.5f, AtomicItems.REFINED_URANIUM_NUGGET.get(), 1)
    );

    public AtomicWashingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAtomic.MODID);
    }
}
