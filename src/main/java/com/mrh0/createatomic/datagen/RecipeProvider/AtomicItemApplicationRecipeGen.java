package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicBlocks;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.data.recipe.ItemApplicationRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AtomicItemApplicationRecipeGen extends ItemApplicationRecipeGen {

    // Apply Reactor Hull Plate to a Fluid Tank → Reactor Casing
    GeneratedRecipe REACTOR_CASING = create(
        CreateAtomic.asResource("item_application/reactor_casing_from_tank"),
        b -> b.require(AllBlocks.FLUID_TANK.get())
              .require(AtomicItems.DENSE_ALLOY.get())
              .output(AtomicBlocks.REACTOR_CASING.get())
    );

    public AtomicItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAtomic.MODID);
    }
}
