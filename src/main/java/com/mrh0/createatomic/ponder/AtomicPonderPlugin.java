package com.mrh0.createatomic.ponder;

import com.mrh0.createatomic.CreateAtomic;

import net.createmod.ponder.api.registration.IndexExclusionHelper;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class AtomicPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateAtomic.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        AtomicPonders.registerScenes(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        AtomicPonders.registerTags(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
        // helper.registerSharedText("rpm8", "8 RPM");
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
        // helper.excludeBlockVariants(ValveHandleBlock.class, AllBlocks.COPPER_VALVE_HANDLE.get());
    }
}
