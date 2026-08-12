package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.datagen.TagProvider.CATagRegister;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AtomicMechanicalCrafterRecipeGen extends MechanicalCraftingRecipeGen {
    public AtomicMechanicalCrafterRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateAtomic.MODID);
    }

    GeneratedRecipe
    ALTERNATOR = create(AtomicItems.HEATSINK::get).recipe(b -> b
            .key('C', CATagRegister.Items.PLATES_COPPER)
            .patternLine("CCCC")
    );
}
