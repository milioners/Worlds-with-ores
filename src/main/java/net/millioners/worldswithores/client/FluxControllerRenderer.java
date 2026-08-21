package net.millioners.worldswithores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.millioners.worldswithores.block.FluxControllerBlock;
import net.millioners.worldswithores.blockentity.FluxControllerBlockEntity;
import net.millioners.worldswithores.registry.ModItems;

public class FluxControllerRenderer implements BlockEntityRenderer<FluxControllerBlockEntity> {
    private final ItemRenderer itemRenderer;

    public FluxControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FluxControllerBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        if (!be.isFormed()) {
            return;
        }

        float time = (be.getLevel() != null ? be.getLevel().getGameTime() : 0) + partialTick;
        float speed = be.isOverheated() ? 0.55F : (be.isBurning() ? 2.2F : 1.0F);
        float spin = time * 3.5F * speed;
        float bob = Mth.sin(time * 0.12F) * 0.06F;
        float pulse = 0.95F + 0.08F * Mth.sin(time * 0.2F);
        if (be.isBurning()) {
            spin *= 1.8F;
            pulse += 0.05F;
        }

        Direction facing = be.getBlockState().getValue(FluxControllerBlock.FACING);
        Direction inward = facing.getOpposite();
        double coreX = 0.5D + inward.getStepX() * 2.0D;
        double coreZ = 0.5D + inward.getStepZ() * 2.0D;
        int light = LightTexture.FULL_BRIGHT;
        ItemStack core = new ItemStack(ModItems.FLUX_CORE.get());

        pose.pushPose();
        pose.translate(coreX, 0.55D + bob, coreZ);
        pose.mulPose(Axis.YP.rotationDegrees(spin));
        pose.mulPose(Axis.XP.rotationDegrees(18.0F));
        pose.scale(pulse, pulse, pulse);
        this.itemRenderer.renderStatic(core, ItemDisplayContext.FIXED, light, packedOverlay, pose, buffers,
                be.getLevel(), 0);
        pose.popPose();

        renderRing(pose, buffers, be, core, coreX, coreZ, bob, spin, 1.15F, Axis.XP, packedOverlay);
        renderRing(pose, buffers, be, core, coreX, coreZ, bob, -spin * 0.73F, 1.38F, Axis.ZP, packedOverlay);
        renderRing(pose, buffers, be, core, coreX, coreZ, bob, spin * 0.46F, 1.62F, Axis.YP, packedOverlay);

        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians(spin * 1.4F + i * 90.0F);
            pose.pushPose();
            pose.translate(coreX + Math.cos(angle) * 0.9D, 0.58D + bob + Math.sin(angle * 2.0D) * 0.18D,
                    coreZ + Math.sin(angle) * 0.9D);
            pose.mulPose(Axis.YP.rotationDegrees(-spin * 2.0F));
            pose.scale(0.24F, 0.24F, 0.24F);
            itemRenderer.renderStatic(core, ItemDisplayContext.FIXED, light, packedOverlay, pose, buffers,
                    be.getLevel(), i + 1);
            pose.popPose();
        }
    }

    private void renderRing(PoseStack pose, MultiBufferSource buffers, FluxControllerBlockEntity be, ItemStack core,
                            double x, double z, float bob, float rotation, float scale, Axis axis, int overlay) {
        pose.pushPose();
        pose.translate(x, 0.55D + bob * 0.5F, z);
        pose.mulPose(axis.rotationDegrees(rotation));
        pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
        pose.scale(scale, scale, scale);
        itemRenderer.renderStatic(core, ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, overlay, pose, buffers,
                be.getLevel(), 0);
        pose.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FluxControllerBlockEntity be) {
        return true;
    }
}
