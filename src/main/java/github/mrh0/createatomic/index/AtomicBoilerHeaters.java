package github.mrh0.createatomic.index;

import com.simibubi.create.content.fluids.tank.BoilerHeaters;

public class AtomicBoilerHeaters {
    public static void register() {
        BoilerHeaters.registerHeater(AtomicBlocks.RAW_URANIUM_BLOCK.get(), (level, pos, state) -> 0);
        BoilerHeaters.registerHeater(AtomicBlocks.RADIOISOTOPE_HEAT_GENERATOR.get(), (level, pos, state) -> 1);
    }
}
