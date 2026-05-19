package github.mrh0.createatomic.blocks.turbine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import github.mrh0.createatomic.index.AtomicPartials;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class TurbineRenderer extends KineticBlockEntityRenderer<TurbineBlockEntity> {

    public TurbineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TurbineBlockEntity be, float partialTicks, PoseStack ms,
                               MultiBufferSource buffer, int light, int overlay) {
        Direction facing = be.getBlockState().getValue(TurbineBlock.FACING);
        Direction intakeSide = facing.getOpposite();

        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        int shaftLight = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(facing));

        SuperByteBuffer shaftHalf = CachedBuffers.partialFacing(
                AllPartialModels.SHAFT_HALF, be.getBlockState(), facing);

        SuperByteBuffer blades = CachedBuffers.partialFacing(
                AtomicPartials.TURBINE_BLADE, be.getBlockState(), intakeSide);

        float time  = AnimationTickHolder.getRenderTime(be.getLevel());
        float speed = be.getSpeed() * 3f;
        if (speed > 0) speed = Mth.clamp(speed, 60, 64 * 20);
        if (speed < 0) speed = Mth.clamp(speed, -64 * 20, -60);
        float angle = (float) Math.toRadians((time * speed * 3f / 10f) % 360f);

        standardKineticRotationTransform(shaftHalf, be, shaftLight).renderInto(ms, vb);
        kineticRotationTransform(blades, be, facing.getAxis(), angle, shaftLight).renderInto(ms, vb);
    }
}
