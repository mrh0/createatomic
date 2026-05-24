package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.datagen.TagProvider.CATagRegister;
import com.mrh0.createatomic.index.AtomicBlocks;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class AtomicCraftingRecipeGen extends RecipeProvider {

    public AtomicCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicItems.PLUTONIUM_INGOT.get())
            .pattern("PPP")
            .pattern("PPP")
            .pattern("PPP")
            .define('P', CATagRegister.Items.NUGGETS_PLUTONIUM)
            .unlockedBy("has_plutonium_nugget", has(AtomicItems.PLUTONIUM_NUGGET.get()))
            .save(output, CreateAtomic.asResource("crafting/plutonium_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.PLUTONIUM_NUGGET.get(), 9)
            .requires(CATagRegister.Items.INGOTS_PLUTONIUM)
            .unlockedBy("has_plutonium_ingot", has(AtomicItems.PLUTONIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/plutonium_nuggets"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get())
            .pattern("PPP")
            .pattern("PPP")
            .pattern("PPP")
            .define('P', CATagRegister.Items.NUGGETS_URANIUM)
            .unlockedBy("has_uranium_nugget", has(AtomicItems.URANIUM_NUGGET.get()))
            .save(output, CreateAtomic.asResource("crafting/uraniumm_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.URANIUM_NUGGET.get(), 9)
            .requires(CATagRegister.Items.INGOTS_URANIUM)
            .unlockedBy("has_uranium_ingot", has(AtomicItems.URANIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/uranium_nuggets"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicItems.REFINED_URANIUM_INGOT.get())
            .pattern("PPP")
            .pattern("PPP")
            .pattern("PPP")
            .define('P', CATagRegister.Items.NUGGETS_REFINED_URANIUM)
            .unlockedBy("has_refined_uranium_nugget", has(AtomicItems.REFINED_URANIUM_NUGGET.get()))
            .save(output, CreateAtomic.asResource("crafting/refined_uranium_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.REFINED_URANIUM_NUGGET.get(), 9)
            .requires(CATagRegister.Items.INGOTS_REFINED_URANIUM)
            .unlockedBy("has_refined_uranium_ingot", has(AtomicItems.REFINED_URANIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/refined_uranium_nuggets"));

        
        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicItems.RAW_URANIUM.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.7f, 200)
            .unlockedBy("has_raw_uranium", has(AtomicItems.RAW_URANIUM.get()))
            .save(output, CreateAtomic.asResource("smelting/uranium_from_raw_uranium"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicItems.RAW_URANIUM.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.7f, 100)
            .unlockedBy("has_raw_uranium", has(AtomicItems.RAW_URANIUM.get()))
            .save(output, CreateAtomic.asResource("blasting/uranium_from_raw_uranium"));

        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AllItems.CRUSHED_URANIUM.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.7f, 200)
            .unlockedBy("has_crushed_uranium", has(AllItems.CRUSHED_URANIUM.get()))
            .save(output, CreateAtomic.asResource("smelting/uranium_from_crushed_uranium"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AllItems.CRUSHED_URANIUM.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.7f, 100)
            .unlockedBy("has_crushed_uranium", has(AllItems.CRUSHED_URANIUM.get()))
            .save(output, CreateAtomic.asResource("blasting/uranium_from_crushed_uranium"));

        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicBlocks.URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 1.0f, 200)
            .unlockedBy("has_uranium_ore", has(AtomicBlocks.URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("smelting/uranium_from_uranium_ore"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicBlocks.URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 1.0f, 100)
            .unlockedBy("has_uranium_ore", has(AtomicBlocks.URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("blasting/uranium_from_uranium_ore"));

        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 1.0f, 200)
            .unlockedBy("has_deepslate_uranium_ore", has(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("smelting/uranium_from_deepslate_uranium_ore"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 1.0f, 100)
            .unlockedBy("has_deepslate_uranium_ore", has(AtomicBlocks.DEEPSLATE_URANIUM_ORE.get()))
            .save(output, CreateAtomic.asResource("blasting/uranium_from_deepslate_uranium_ore"));

        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicItems.DEPLETED_FUEL_ROD.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.3f, 200)
            .unlockedBy("has_depleted_fuel_rod", has(AtomicItems.DEPLETED_FUEL_ROD.get()))
            .save(output, CreateAtomic.asResource("smelting/uranium_from_depleted_rod"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicItems.DEPLETED_FUEL_ROD.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.3f, 100)
            .unlockedBy("has_depleted_fuel_rod", has(AtomicItems.DEPLETED_FUEL_ROD.get()))
            .save(output, CreateAtomic.asResource("blasting/uranium_from_depleted_rod"));
    }
}
