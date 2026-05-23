package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AtomicWashingRecipeGen extends WashingRecipeGen {

    // Splash raw_uranium → uranium nuggets (less efficient than crushing)
    GeneratedRecipe RAW_URANIUM = create(CreateAtomic.MODID, AtomicItems.RAW_URANIUM::get, b -> b
        .output(AtomicItems.DEPLETED_URANIUM_NUGGET.get(), 5)
        .output(0.5f, AtomicItems.DEPLETED_URANIUM_NUGGET.get(), 1)
        .output(0.25f, AtomicItems.URANIUM_NUGGET.get(), 1)
    );

    public AtomicWashingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAtomic.MODID);
    }
}
