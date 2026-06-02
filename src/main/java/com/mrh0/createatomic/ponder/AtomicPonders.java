package com.mrh0.createatomic.ponder;

import com.mrh0.createatomic.CreateAtomic;
import com.mrh0.createatomic.index.AtomicBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class AtomicPonders {
    public static final ResourceLocation ATOMIC = CreateAtomic.asResource("atomic");

    public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
            HELPER.registerTag(ATOMIC)
				.addToIndex()
				.item(AtomicBlocks.REACTOR_CASING.get(), true, false)
				.title("Reactor Blocks")
				.description("Reactor components")
				.register();
    }

    public static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.addStoryBoard(AtomicBlocks.REACTOR_CASING, "reactor", PonderScenes::reactor, ATOMIC);
    }
}
