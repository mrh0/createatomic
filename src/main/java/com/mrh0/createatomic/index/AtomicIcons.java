package com.mrh0.createatomic.index;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrh0.createatomic.CreateAtomic;
import com.simibubi.create.foundation.gui.AllIcons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

// Create's value boxes only accept AllIcons, so this subclass redirects both render paths to our own atlas.
// Icons are 16x16 cells in textures/gui/icons.png, addressed by (column, row).
public class AtomicIcons extends AllIcons {

    public static final ResourceLocation ICON_ATLAS = CreateAtomic.asResource("textures/gui/icons.png");
    public static final int ICON_ATLAS_SIZE = 64;

    public static final AtomicIcons
        I_TEMPERATURE = new AtomicIcons(0, 0),
        I_HULL        = new AtomicIcons(1, 0),
        I_WATER       = new AtomicIcons(2, 0),
        I_FUEL        = new AtomicIcons(3, 0);

    private final int iconX;
    private final int iconY;

    public AtomicIcons(int x, int y) {
        super(x, y);
        iconX = x * 16;
        iconY = y * 16;
    }

    // Value settings screen (scroll selection cursor).
    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(ICON_ATLAS, x, y, 0, iconX, iconY, 16, 16, ICON_ATLAS_SIZE, ICON_ATLAS_SIZE);
    }

    // In-world value box.
    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int color) {
        VertexConsumer builder = buffer.getBuffer(RenderType.text(ICON_ATLAS));
        Matrix4f matrix = ms.last().pose();
        int r = (color >> 16) & 0xFF, g = (color >> 8) & 0xFF, b = color & 0xFF;
        int light = LightTexture.FULL_BRIGHT;

        float u1 = iconX / (float) ICON_ATLAS_SIZE;
        float u2 = (iconX + 16) / (float) ICON_ATLAS_SIZE;
        float v1 = iconY / (float) ICON_ATLAS_SIZE;
        float v2 = (iconY + 16) / (float) ICON_ATLAS_SIZE;

        builder.addVertex(matrix, 0, 0, 0).setColor(r, g, b, 255).setUv(u1, v1).setLight(light);
        builder.addVertex(matrix, 0, 1, 0).setColor(r, g, b, 255).setUv(u1, v2).setLight(light);
        builder.addVertex(matrix, 1, 1, 0).setColor(r, g, b, 255).setUv(u2, v2).setLight(light);
        builder.addVertex(matrix, 1, 0, 0).setColor(r, g, b, 255).setUv(u2, v1).setLight(light);
    }
}
