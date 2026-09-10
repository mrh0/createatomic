package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AtomicPressingRecipeGen extends PressingRecipeGen {

    /*GeneratedRecipe DENSE_ALLOY_PLATE = create(CreateAtomic.MODID, AtomicItems.DENSE_ALLOY::get, b -> b
        .output(AtomicItems.HULL_PLATE.get())
    );

    GeneratedRecipe GRAPHITE_PLATE = create(CreateAtomic.MODID, AtomicItems.GRAPHITE_DUST::get, b -> b
        .output(AtomicItems.GRAPHITE_SHEET.get())
    );*/

    GeneratedRecipe RAD_PILL = create(CreateAtomic.MODID, AtomicItems.RAD_PASTE::get, b -> b
        .output(AtomicItems.RAD_PILL.get())
    );

    public AtomicPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAtomic.MODID);
    }
}
