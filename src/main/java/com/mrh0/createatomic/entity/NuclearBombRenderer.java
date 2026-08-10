package com.mrh0.createatomic.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createatomic.index.AtomicBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class NuclearBombRenderer extends EntityRenderer<NuclearBombEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public NuclearBombRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.blockRenderer = ctx.getBlockRenderDispatcher();
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(NuclearBombEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        int fuse = entity.getFuse();

        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);

        // Shake in the last 40 ticks
        if (fuse < 40) {
            float shake = (40 - fuse) / 40.0f * 0.08f;
            poseStack.translate(
                    (entity.level().random.nextFloat() - 0.5f) * shake,
                    0,
                    (entity.level().random.nextFloat() - 0.5f) * shake
            );
        }

        poseStack.translate(-0.5, -0.5, -0.5);

        // Flash white every 5 ticks when fuse is short
        int overlay = (fuse < 80 && (fuse / 5) % 2 == 0)
                ? OverlayTexture.pack(0, 10)
                : OverlayTexture.NO_OVERLAY;

        this.blockRenderer.renderSingleBlock(
                AtomicBlocks.NUCLEAR_BOMB.getDefaultState(),
                poseStack, bufferSource, packedLight, overlay
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ResourceLocation getTextureLocation(NuclearBombEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
