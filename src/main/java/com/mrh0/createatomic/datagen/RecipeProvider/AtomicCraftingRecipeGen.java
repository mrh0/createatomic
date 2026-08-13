package com.mrh0.createatomic.datagen.RecipeProvider;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.datagen.TagProvider.CATagRegister;
import com.mrh0.createatomic.index.AtomicBlocks;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class AtomicCraftingRecipeGen extends RecipeProvider {

    public AtomicCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // Storage
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicItems.DENSE_ALLOY.get())
            .pattern("PPP")
            .pattern("PPP")
            .pattern("PPP")
            .define('P', CATagRegister.Items.NUGGETS_DENSE_ALLOY)
            .unlockedBy("has_dense_alloy_nugget", has(AtomicItems.DENSE_ALLOY_NUGGET.get()))
            .save(output, CreateAtomic.asResource("crafting/dense_alloy_from_nuggets"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.DENSE_ALLOY_NUGGET.get(), 9)
            .requires(CATagRegister.Items.PLATES_DENSE_ALLOY)
            .unlockedBy("has_dense_alloy", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/dense_alloy_nuggets"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.RAW_URANIUM_BLOCK.get())
            .pattern("PPP")
            .pattern("PPP")
            .pattern("PPP")
            .define('P', CATagRegister.Items.RAW_MATERIALS_URANIUM)
            .unlockedBy("has_raw_materials_uranium", has(CATagRegister.Items.RAW_MATERIALS_URANIUM))
            .save(output, CreateAtomic.asResource("crafting/raw_uranium_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.RAW_URANIUM.get(), 9)
            .requires(AtomicBlocks.RAW_URANIUM_BLOCK.get())
            .unlockedBy("has_raw_uranium_block", has(AtomicBlocks.RAW_URANIUM_BLOCK.get()))
            .save(output, CreateAtomic.asResource("crafting/raw_uranium_from_block"));

        
        // Storage blocks — compress
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.PLUTONIUM_BLOCK.get())
            .pattern("PPP")
            .pattern("PPP")
            .pattern("PPP")
            .define('P', CATagRegister.Items.INGOTS_PLUTONIUM)
            .unlockedBy("has_plutonium_ingot", has(AtomicItems.PLUTONIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/plutonium_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.PLUTONIUM_INGOT.get(), 9)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_PLUTONIUM)
            .unlockedBy("has_plutonium_block", has(AtomicBlocks.PLUTONIUM_BLOCK.get()))
            .save(output, CreateAtomic.asResource("crafting/plutonium_ingots_from_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.URANIUM_BLOCK.get())
            .pattern("UUU")
            .pattern("UUU")
            .pattern("UUU")
            .define('U', CATagRegister.Items.INGOTS_URANIUM)
            .unlockedBy("has_uranium_ingot", has(AtomicItems.URANIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/uranium_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 9)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_URANIUM)
            .unlockedBy("has_uranium_block", has(AtomicBlocks.URANIUM_BLOCK.get()))
            .save(output, CreateAtomic.asResource("crafting/uranium_ingots_from_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.REFINED_URANIUM_BLOCK.get())
            .pattern("RRR")
            .pattern("RRR")
            .pattern("RRR")
            .define('R', CATagRegister.Items.INGOTS_REFINED_URANIUM)
            .unlockedBy("has_refined_uranium_ingot", has(AtomicItems.REFINED_URANIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/refined_uranium_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.REFINED_URANIUM_INGOT.get(), 9)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_REFINED_URANIUM)
            .unlockedBy("has_refined_uranium_block", has(AtomicBlocks.REFINED_URANIUM_BLOCK.get()))
            .save(output, CreateAtomic.asResource("crafting/refined_uranium_ingots_from_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.DENSE_ALLOY_BLOCK.get())
            .pattern("DDD")
            .pattern("DDD")
            .pattern("DDD")
            .define('D', CATagRegister.Items.PLATES_DENSE_ALLOY)
            .unlockedBy("has_dense_alloy", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/dense_alloy_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.DENSE_ALLOY.get(), 9)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_DENSE_ALLOY)
            .unlockedBy("has_dense_alloy_block", has(AtomicBlocks.DENSE_ALLOY_BLOCK.get()))
            .save(output, CreateAtomic.asResource("crafting/dense_alloy_from_block"));

        // Ores and Blocks
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

        /*SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(AtomicItems.DEPLETED_FUEL_ROD.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.3f, 200)
            .unlockedBy("has_depleted_fuel_rod", has(AtomicItems.DEPLETED_FUEL_ROD.get()))
            .save(output, CreateAtomic.asResource("smelting/uranium_from_depleted_rod"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(AtomicItems.DEPLETED_FUEL_ROD.get()), RecipeCategory.MISC, AtomicItems.URANIUM_INGOT.get(), 0.3f, 100)
            .unlockedBy("has_depleted_fuel_rod", has(AtomicItems.DEPLETED_FUEL_ROD.get()))
            .save(output, CreateAtomic.asResource("blasting/uranium_from_depleted_rod"));*/

        // Reactor Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.REACTOR_REDSTONE_INTERFACE.get())
            .pattern("E")
            .pattern("D")
            .define('E', AllItems.ELECTRON_TUBE.get())
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_dense_alloy_plate", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/reactor_redstone_interface"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.ROD_ASSEMBLY.get())
            .pattern("I I")
            .pattern("IDI")
            .pattern("I I")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_dense_alloy_plate", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/rod_assembly"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.TURBINE.get())
            .pattern("DCC")
            .pattern("FPP")
            .pattern("DCC")
            .define('P', AllItems.PROPELLER.get())
            .define('F', AllBlocks.SHAFT.get())
            .define('C', AllItems.COPPER_SHEET.get())
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_dense_alloy_plate", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/turbine"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.TURBINE.get())
            .pattern("CCD")
            .pattern("PPF")
            .pattern("CCD")
            .define('P', AllItems.PROPELLER.get())
            .define('F', AllBlocks.SHAFT.get())
            .define('C', CATagRegister.Items.PLATES_COPPER)
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_dense_alloy_plate", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/turbine_alt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.RADIOISOTOPE_HEAT_GENERATOR_KINDLED.get())
            .pattern("CHC")
            .pattern("PPP")
            .pattern(" R ")
            .define('H', AtomicItems.HEATSINK)
            .define('P', AtomicItems.PLUTONIUM_INGOT.get())
            .define('R', AtomicBlocks.REACTOR_CASING.get())
            .define('C', CATagRegister.Items.PLATES_COPPER)
            .unlockedBy("has_dense_alloy_plate", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/radioisotope_heat_generator"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.CRUDE_NUCLEAR_BOMB.get(), 1)
            .pattern("GDG")
            .pattern("GPG")
            .pattern("GDG")
            .define('G', Items.GUNPOWDER)
            .define('P', CATagRegister.Items.INGOTS_PLUTONIUM)
            .define('D', AtomicItems.DENSE_ALLOY)
            .unlockedBy("has_plutonium", has(CATagRegister.Items.INGOTS_PLUTONIUM))
            .save(output, CreateAtomic.asResource("crafting/crude_nuclear_bomb"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AtomicBlocks.RTG.get(), 1)
            .pattern("HDH")
            .pattern("PPP")
            .pattern(" R ")
            .define('H', AtomicItems.HEATSINK)
            .define('P', CATagRegister.Items.INGOTS_PLUTONIUM)
            .define('D', AtomicItems.DENSE_ALLOY)
            .define('R', AtomicBlocks.REACTOR_CASING.get())
            .unlockedBy("has_plutonium", has(CATagRegister.Items.INGOTS_PLUTONIUM))
            .save(output, CreateAtomic.asResource("crafting/rtg"));

        // Reactor Components
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.FUEL_ROD.get(), 1)
            .requires(CATagRegister.Items.INGOTS_REFINED_URANIUM)
            .requires(AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_refined_uranium_ingot", has(AtomicItems.REFINED_URANIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/fuel_rod"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.PLUTONIUM_FUEL_ROD.get(), 1)
            .requires(CATagRegister.Items.INGOTS_PLUTONIUM)
            .requires(AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_plutonium_ingot", has(AtomicItems.PLUTONIUM_INGOT.get()))
            .save(output, CreateAtomic.asResource("crafting/plutonium_rod"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.SMALL_CONTROL_ROD.get(), 1)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_IRON)
            .requires(AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_refined_uranium_ingot", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/small_control_rod"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.LARGE_CONTROL_ROD.get(), 1)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_IRON)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_IRON)
            .requires(AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_dense_alloy_plate", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/large_control_rod"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AtomicItems.NEUTRON_REFLECTOR.get(), 1)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_COAL)
            .requires(CATagRegister.Items.STORAGE_BLOCKS_COAL)
            .requires(AtomicItems.DENSE_ALLOY.get())
            .unlockedBy("has_dense_alloy_plate", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/neutron_reflector"));

        // Hazmat Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AtomicItems.HAZMAT_HELMET.get())
            .pattern("YDY")
            .pattern("DGD")
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .define('Y', Tags.Items.DYES_YELLOW)
            .define('G', AllItems.GOGGLES.get())
            .unlockedBy("has_dense_alloy", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/hazmat_helmet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AtomicItems.HAZMAT_CHESTPLATE.get())
            .pattern("D D")
            .pattern("YDY")
            .pattern("YDY")
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .define('Y', Tags.Items.DYES_YELLOW)
            .unlockedBy("has_dense_alloy", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/hazmat_chestplate"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AtomicItems.HAZMAT_LEGGINGS.get())
            .pattern("YDY")
            .pattern("D D")
            .pattern("Y Y")
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .define('Y', Tags.Items.DYES_YELLOW)
            .unlockedBy("has_dense_alloy", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/hazmat_leggings"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AtomicItems.HAZMAT_BOOTS.get())
            .pattern("Y Y")
            .pattern("D D")
            .define('D', AtomicItems.DENSE_ALLOY.get())
            .define('Y', Tags.Items.DYES_YELLOW)
            .unlockedBy("has_dense_alloy", has(AtomicItems.DENSE_ALLOY.get()))
            .save(output, CreateAtomic.asResource("crafting/hazmat_boots"));
    }
}
