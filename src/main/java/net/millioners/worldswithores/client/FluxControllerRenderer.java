package net.millioners.worldswithores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
        float spin = time * 3.5F;
        float bob = Mth.sin(time * 0.12F) * 0.06F;
        float pulse = 0.95F + 0.08F * Mth.sin(time * 0.2F);
        if (be.isBurning()) {
            spin *= 1.8F;
            pulse += 0.05F;
        }

        pose.pushPose();
        pose.translate(0.5D, 0.55D + bob, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(spin));
        pose.mulPose(Axis.XP.rotationDegrees(18.0F));
        pose.scale(pulse, pulse, pulse);

        ItemStack core = new ItemStack(ModItems.FLUX_CORE.get());
        this.itemRenderer.renderStatic(core, ItemDisplayContext.FIXED, packedLight, packedOverlay, pose, buffers,
                be.getLevel(), 0);
        pose.popPose();

        // outer ring
        pose.pushPose();
        pose.translate(0.5D, 0.55D + bob * 0.5F, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(-spin * 0.65F));
        pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
        pose.scale(1.25F, 1.25F, 1.25F);
        this.itemRenderer.renderStatic(core, ItemDisplayContext.GROUND, packedLight, packedOverlay, pose, buffers,
                be.getLevel(), 0);
        pose.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FluxControllerBlockEntity be) {
        return true;
    }
}
