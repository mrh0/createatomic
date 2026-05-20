package github.mrh0.createatomic.blocks.rod_assembly;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import github.mrh0.createatomic.index.AtomicPartials;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;

public class RodAssemblyRenderer extends SafeBlockEntityRenderer<RodAssemblyBlockEntity> {

    public RodAssemblyRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(RodAssemblyBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        RodConfiguration config = be.getConfig();

        PartialModel partial = switch (config) {
            case FuelRod          -> AtomicPartials.ROD_FUEL;
            case DepletedFuelRod  -> AtomicPartials.ROD_DEPLETED;
            case SmallControlRod  -> AtomicPartials.ROD_CONTROL_SMALL;
            case LargeControlRod  -> AtomicPartials.ROD_CONTROL_LARGE;
            case NeutronReflector -> AtomicPartials.ROD_REFLECTOR;
            default               -> null;
        };

        if (partial == null) return;

        int rodLight = sampleRodLight(be);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        CachedBuffers.partial(partial, be.getBlockState())
                .light(rodLight)
                .renderInto(ms, vb);
    }

    // Sample light from above the rod assembly so the rod isn't unlit inside a reactor.
    private int sampleRodLight(RodAssemblyBlockEntity be) {
        if (be.getLevel() == null) return LightTexture.FULL_BRIGHT;
        BlockPos above = be.getBlockPos().above();
        int sky   = be.getLevel().getBrightness(LightLayer.SKY,   above);
        int block = be.getLevel().getBrightness(LightLayer.BLOCK, above);
        block = Math.max(block, 4);
        return LightTexture.pack(block, sky);
    }
}
