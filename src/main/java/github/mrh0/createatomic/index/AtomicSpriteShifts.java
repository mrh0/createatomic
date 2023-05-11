package github.mrh0.createatomic.index;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import github.mrh0.createatomic.CreateAtomic;
import net.minecraft.resources.ResourceLocation;

import static com.simibubi.create.foundation.block.connected.AllCTTypes.RECTANGLE;
import static com.simibubi.create.foundation.block.connected.CTSpriteShifter.getCT;

public class AtomicSpriteShifts {
    public static final CTSpriteShiftEntry
            REACTOR_CASING = getCT(
                RECTANGLE,
                new ResourceLocation(CreateAtomic.MODID, "block/reactor_casing/block"),
                new ResourceLocation(CreateAtomic.MODID, "block/reactor_casing/block_connected")
            ),
            REACTOR_CASING_TOP = getCT(
                RECTANGLE,
                new ResourceLocation(CreateAtomic.MODID, "block/reactor_casing/block_top"),
                new ResourceLocation(CreateAtomic.MODID, "block/reactor_casing/block_top_connected")
            );
}