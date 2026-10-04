package com.mrh0.createatomic.datagen;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class AtomicBlockStateGen {

    // Full cube attached to a Reactor Casing. directionalBlock points the model's top along FACING,
    // so "back" (top) touches the casing and "front" (bottom) faces outward.
    // Textures: block/<name>/side<suffix>, block/<name>/front<suffix> and block/<name>/back.
    public static <T extends Block> void reactorAttachment(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                           Function<BlockState, String> stateSuffix) {
        String name = ctx.getName();
        prov.directionalBlock(ctx.get(), state -> {
            String suffix = stateSuffix.apply(state);
            return prov.models().cubeBottomTop(name + suffix,
                    prov.modLoc("block/" + name + "/side" + suffix),
                    prov.modLoc("block/" + name + "/front" + suffix),
                    prov.modLoc("block/" + name + "/back"));
        });
    }
}
