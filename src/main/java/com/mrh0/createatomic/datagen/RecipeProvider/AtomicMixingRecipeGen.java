package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.datagen.TagProvider.CATagRegister;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.concurrent.CompletableFuture;

public class AtomicMixingRecipeGen extends MixingRecipeGen {

    GeneratedRecipe DENSE_ALLOY = create(CreateAtomic.asResource("mixing/dense_alloy"), b -> b
        .require(Tags.Items.INGOTS_IRON)
        .require(Tags.Items.INGOTS_IRON)
        .require(Tags.Items.INGOTS_COPPER)
        .require(ItemTags.COALS)
        .output(AtomicItems.DENSE_ALLOY.get())
        .requiresHeat(HeatCondition.SUPERHEATED)
    );

    GeneratedRecipe DENSE_ALLOY_ALT = create(CreateAtomic.asResource("mixing/dense_alloy_alt"), b -> b
        .require(CATagRegister.Items.INGOTS_LEAD)
        .require(Tags.Items.INGOTS_COPPER)
        .require(ItemTags.COALS)
        .output(AtomicItems.DENSE_ALLOY.get())
        .withCondition(new NotCondition(
            new TagEmptyCondition(CATagRegister.Items.INGOTS_LEAD)
        ))
        .requiresHeat(HeatCondition.SUPERHEATED)
    );

    public AtomicMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAtomic.MODID);
    }
}
