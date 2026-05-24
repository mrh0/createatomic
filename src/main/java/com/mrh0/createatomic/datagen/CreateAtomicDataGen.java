package com.mrh0.createatomic.datagen;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.datagen.RecipeProvider.AtomicCraftingRecipeGen;
import com.mrh0.createatomic.datagen.RecipeProvider.AtomicCrushingRecipeGen;
import com.mrh0.createatomic.datagen.RecipeProvider.AtomicWashingRecipeGen;
import com.mrh0.createatomic.datagen.TagProvider.CABlockTagProvider;
import com.mrh0.createatomic.datagen.TagProvider.CAFluidTagProvider;
import com.mrh0.createatomic.datagen.TagProvider.CAItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = CreateAtomic.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CreateAtomicDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // Tags
        BlockTagsProvider blockTags = new CABlockTagProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new CAFluidTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new CAItemTagProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

        // Recipes
        generator.addProvider(event.includeServer(), new AtomicCraftingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new AtomicCrushingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new AtomicWashingRecipeGen(output, lookupProvider));
    }
}
