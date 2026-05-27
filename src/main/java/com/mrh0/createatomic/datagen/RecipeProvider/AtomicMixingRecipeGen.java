package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class AtomicMixingRecipeGen extends MixingRecipeGen {

    GeneratedRecipe HULL_PLATE = create(CreateAtomic.asResource("mixing/hull_plate"), b -> b
        .require(Tags.Items.INGOTS_IRON)
        .require(Tags.Items.INGOTS_IRON)
        .require(Tags.Items.INGOTS_COPPER)
        .require(ItemTags.COALS)
        .output(AtomicItems.HULL_PLATE.get())
        .requiresHeat(HeatCondition.HEATED)
    );

    public AtomicMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAtomic.MODID);
    }
}
