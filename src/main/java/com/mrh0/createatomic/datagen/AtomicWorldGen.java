package com.mrh0.createatomic.datagen;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class AtomicWorldGen {

    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_ORE_CF =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, CreateAtomic.asResource("uranium_ore"));

    public static final ResourceKey<PlacedFeature> URANIUM_ORE_PF =
        ResourceKey.create(Registries.PLACED_FEATURE, CreateAtomic.asResource("uranium_ore"));

    public static final ResourceKey<BiomeModifier> ADD_URANIUM_ORE =
        ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, CreateAtomic.asResource("add_uranium_ore"));

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(URANIUM_ORE_CF, new ConfiguredFeature<>(Feature.ORE,
            new OreConfiguration(List.of(
                OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                    AtomicBlocks.URANIUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
                    AtomicBlocks.DEEPSLATE_URANIUM_ORE.get().defaultBlockState())
            ), 7)
        ));
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(URANIUM_ORE_PF, new PlacedFeature(
            features.getOrThrow(URANIUM_ORE_CF),
            List.of(
                CountPlacement.of(4),
                InSquarePlacement.spread(),
                HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(16)),
                BiomeFilter.biome()
            )
        ));
    }

    public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        context.register(ADD_URANIUM_ORE, new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(placedFeatures.getOrThrow(URANIUM_ORE_PF)),
            GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }
}
