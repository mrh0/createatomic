package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicBlocks;
import com.mrh0.createatomic.index.AtomicItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class AtomicSmeltingRecipeGen extends RecipeProvider {

    public AtomicSmeltingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // raw_uranium → refined_uranium
        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicItems.RAW_URANIUM.get()), RecipeCategory.MISC, AtomicItems.REFINED_URANIUM.get(), 0.7f, 200)
            .unlockedBy("has_raw_uranium", has(AtomicItems.RAW_URANIUM.get()))
            .save(output, CreateAtomic.asResource("smelting/refined_uranium_from_raw_uranium"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicItems.RAW_URANIUM.get()), RecipeCategory.MISC, AtomicItems.REFINED_URANIUM.get(), 0.7f, 100)
            .unlockedBy("has_raw_uranium", has(AtomicItems.RAW_URANIUM.get()))
            .save(output, CreateAtomic.asResource("blasting/refined_uranium_from_raw_uranium"));

        // uranium_ore → refined_uranium
        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicBlocks.URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.REFINED_URANIUM.get(), 1.0f, 200)
            .unlockedBy("has_uranium_ore", has(AtomicBlocks.URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("smelting/refined_uranium_from_uranium_ore"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicBlocks.URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.REFINED_URANIUM.get(), 1.0f, 100)
            .unlockedBy("has_uranium_ore", has(AtomicBlocks.URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("blasting/refined_uranium_from_uranium_ore"));

        // deepslate_uranium_ore → refined_uranium
        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.REFINED_URANIUM.get(), 1.0f, 200)
            .unlockedBy("has_deepslate_uranium_ore", has(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("smelting/refined_uranium_from_deepslate_uranium_ore"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.REFINED_URANIUM.get(), 1.0f, 100)
            .unlockedBy("has_deepslate_uranium_ore", has(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("blasting/refined_uranium_from_deepslate_uranium_ore"));

        // depleted_uranium_rod → depleted_uranium (recycle spent fuel rods)
        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicItems.DEPLETED_FUEL_ROD.get()), RecipeCategory.MISC, AtomicItems.DEPLETED_URANIUM.get(), 0.3f, 200)
            .unlockedBy("has_depleted_uranium_rod", has(AtomicItems.DEPLETED_FUEL_ROD.get()))
            .save(output, CreateAtomic.asResource("smelting/depleted_uranium_from_depleted_rod"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicItems.DEPLETED_FUEL_ROD.get()), RecipeCategory.MISC, AtomicItems.DEPLETED_URANIUM.get(), 0.3f, 100)
            .unlockedBy("has_depleted_uranium_rod", has(AtomicItems.DEPLETED_FUEL_ROD.get()))
            .save(output, CreateAtomic.asResource("blasting/depleted_uranium_from_depleted_rod"));
    }
}
