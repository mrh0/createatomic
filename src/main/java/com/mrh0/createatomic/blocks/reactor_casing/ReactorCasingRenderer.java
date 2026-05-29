package com.mrh0.createatomic.blocks.reactor_casing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.Util;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import com.mrh0.createatomic.index.AtomicPartials;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorCasingRenderer extends SafeBlockEntityRenderer<ReactorCasingBlockEntity> {

    public ReactorCasingRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(ReactorCasingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!be.isController()) return;
        renderDial(be, partialTicks, ms, buffer, light, overlay);
        be.observe();
    }

    protected void renderDial(ReactorCasingBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (te.gauge == null) return;

        BlockState blockState = te.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        ms.pushPose();
        TransformStack<PoseTransformStack> msr = TransformStack.of(ms);
        msr.translate(te.width / 2f, te.height - 0.5f, te.width / 2f);

        float dialPivotY = 6f / 16f;
        float dialPivotZ = 8f / 16f;
        float progress = te.gauge.getValue(partialTicks);

        // When temperature exceeds 315C the needle pegs at max and shakes.
        float shake = 0f;
        if (te.reactorHeat > 315) {
            float excess = (te.reactorHeat - 315f) / 290f;
            float amplitude = Math.min(10f, excess * 10f);
            float time = (float)(Util.getMillis() % 4000) / 1000f;
            shake = (float)Math.sin(time * (12f + excess * 20f)) * amplitude;
        }

        int gaugeLight = sampleTopLight(te);

        for (Direction d : Iterate.horizontalDirections) {
            ms.pushPose();
            float yRot = -d.toYRot() - 90;
            CachedBuffers.partial(AtomicPartials.REACTOR_GUAGE, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(te.width / 2f - 6 / 16f, 0, 0)
                    .light(gaugeLight)
                    .renderInto(ms, vb);
            CachedBuffers.partial(AtomicPartials.REACTOR_DIAL, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(te.width / 2f - 6 / 16f, 0, 0)
                    .translate(0, dialPivotY, dialPivotZ)
                    .rotateXDegrees(-145 * progress + 90 + shake)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(gaugeLight)
                    .renderInto(ms, vb);
            ms.popPose();
        }

        ms.popPose();
    }

    private int sampleTopLight(ReactorCasingBlockEntity te) {
        LevelReader level = te.getLevel();
        if (level == null) return LightTexture.FULL_BRIGHT;
        BlockPos topAbove = te.getBlockPos().above(te.height);
        int skyLight   = level.getBrightness(net.minecraft.world.level.LightLayer.SKY,   topAbove);
        int blockLight = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, topAbove);
        blockLight = Math.max(blockLight, 8);
        return LightTexture.pack(blockLight, skyLight);
    }

    @Override
    public boolean shouldRenderOffScreen(ReactorCasingBlockEntity te) {
        return te.isController();
    }
}
