package com.mrh0.createatomic.datagen.TagProvider;

import com.mrh0.createatomic.CreateAtomic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CATagRegister {
    public static class Items {
        public static final TagKey<Item> ORES = commonTags("ores");
        public static final TagKey<Item> ORES_URANIUM = commonTags("ores", "uranium");
        public static final TagKey<Item> ORES_IN_GROUND_STONE = commonTags("ores_in_ground", "stone");
        public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = commonTags("ores_in_ground", "deepslate");
        public static final TagKey<Item> STORAGE_BLOCKS = commonTags("storage_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_URANIUM = commonTags("storage_blocks", "raw_uranium");
        public static final TagKey<Item> STORAGE_BLOCKS_PLUTONIUM = commonTags("storage_blocks", "plutonium");
        public static final TagKey<Item> STORAGE_BLOCKS_URANIUM = commonTags("storage_blocks", "uranium");
        public static final TagKey<Item> STORAGE_BLOCKS_REFINED_URANIUM = commonTags("storage_blocks", "refined_uranium");
        public static final TagKey<Item> STORAGE_BLOCKS_DENSE_ALLOY = commonTags("storage_blocks", "dense_alloy");
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = commonTags("storage_blocks", "iron");
        public static final TagKey<Item> STORAGE_BLOCKS_COAL = commonTags("storage_blocks", "coal");
        public static final TagKey<Item> RAW_MATERIALS = commonTags("raw_materials");
        public static final TagKey<Item> RAW_MATERIALS_URANIUM = commonTags("raw_materials", "uranium");
        public static final TagKey<Item> INGOTS = commonTags("ingots");
        public static final TagKey<Item> INGOTS_IRON = commonTags("ingots", "iron");
        public static final TagKey<Item> INGOTS_REFINED_URANIUM = commonTags("ingots", "refined_uranium");
        public static final TagKey<Item> INGOTS_URANIUM = commonTags("ingots", "uranium");
        public static final TagKey<Item> INGOTS_PLUTONIUM = commonTags("ingots", "plutonium");
        public static final TagKey<Item> INGOTS_LEAD = commonTags("ingots", "lead");
        public static final TagKey<Item> NUGGETS = commonTags("nuggets");
        public static final TagKey<Item> NUGGETS_REFINED_URANIUM = commonTags("nuggets", "refined_uranium");
        public static final TagKey<Item> NUGGETS_URANIUM = commonTags("nuggets", "uranium");
        public static final TagKey<Item> NUGGETS_PLUTONIUM = commonTags("nuggets", "plutonium");
        public static final TagKey<Item> PLATES = commonTags("plates");
        public static final TagKey<Item> PLATES_IRON = commonTags("plates", "iron");
        public static final TagKey<Item> PLATES_COPPER = commonTags("plates", "copper");
        public static final TagKey<Item> PLATES_LEAD = commonTags("plates", "lead");
        public static final TagKey<Item> PLATES_GRAPHITE = commonTags("plates", "graphite");
        public static final TagKey<Item> PLATES_DENSE_ALLOY = commonTags("plates", "dense_alloy");
        public static final TagKey<Item> NUGGETS_DENSE_ALLOY = commonTags("nuggets", "dense_alloy");
        public static final TagKey<Item> DUSTS = commonTags("dusts");
        public static final TagKey<Item> DUSTS_GRAPHITE = commonTags("dusts", "graphite");

        // Mod-specific tags (createatomic: namespace)
        public static final TagKey<Item> CONTROL_RODS = atomicTags("control_rods");
        public static final TagKey<Item> FUEL_RODS = atomicTags("fuel_rods");
        public static final TagKey<Item> REACTOR_COMPONENTS = atomicTags("reactor_components");

        public static TagKey<Item> commonTags(String folder, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", String.format("%s/%s", folder, name)));
        }

        public static TagKey<Item> commonTags(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static TagKey<Item> atomicTags(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CreateAtomic.MODID, name));
        }

        public static TagKey<Item> modTags(String mod, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(mod, name));
        }

        public static TagKey<Item> modTags(String mod, String folder, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(mod, String.format("%s/%s", folder, name)));
        }
    }

    public static class Blocks {
        // Common tags (c: namespace)
        public static final TagKey<Block> ORES = commonTags("ores");
        public static final TagKey<Block> ORES_URANIUM = commonTags("ores", "uranium");
        public static final TagKey<Block> ORES_IN_GROUND_STONE = commonTags("ores_in_ground", "stone");
        public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = commonTags("ores_in_ground", "deepslate");
        public static final TagKey<Block> STORAGE_BLOCKS = commonTags("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_URANIUM = commonTags("storage_blocks", "raw_uranium");
        public static final TagKey<Block> STORAGE_BLOCKS_PLUTONIUM = commonTags("storage_blocks", "plutonium");
        public static final TagKey<Block> STORAGE_BLOCKS_URANIUM = commonTags("storage_blocks", "uranium");
        public static final TagKey<Block> STORAGE_BLOCKS_REFINED_URANIUM = commonTags("storage_blocks", "refined_uranium");
        public static final TagKey<Block> STORAGE_BLOCKS_DENSE_ALLOY = commonTags("storage_blocks", "dense_alloy");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = commonTags("storage_blocks", "iron");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = commonTags("storage_blocks", "coal");

        public static TagKey<Block> commonTags(String folder, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", String.format("%s/%s", folder, name)));
        }

        public static TagKey<Block> commonTags(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static TagKey<Block> modTags(String mod, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(mod, name));
        }

        public static TagKey<Block> modTags(String mod, String folder, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(mod, String.format("%s/%s", folder, name)));
        }
    }

    public static class Fluids {
        public static TagKey<Fluid> commonTags(String folder, String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", String.format("%s/%s", folder, name)));
        }

        public static TagKey<Fluid> commonTags(String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
