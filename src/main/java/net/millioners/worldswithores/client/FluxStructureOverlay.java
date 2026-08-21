package net.millioners.worldswithores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.block.FluxControllerBlock;
import net.millioners.worldswithores.blockentity.FluxMultiblock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldsWithOresMod.MOD_ID, value = Dist.CLIENT)
public final class FluxStructureOverlay {
    private static BlockPos controllerPos;
    private static int layer = -1;

    private FluxStructureOverlay() {}

    public static void cycle(BlockPos pos) {
        if (!pos.equals(controllerPos) || layer < 0) {
            controllerPos = pos.immutable();
            layer = 0;
        } else if (layer < 5) {
            layer++;
        } else {
            layer = -1;
            controllerPos = null;
        }
    }

    public static int mode() {
        return layer;
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS
                || controllerPos == null || layer < 0) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null
                || minecraft.player.distanceToSqr(Vec3.atCenterOf(controllerPos)) > 4096.0D) {
            controllerPos = null;
            layer = -1;
            return;
        }
        BlockState controller = minecraft.level.getBlockState(controllerPos);
        if (!controller.hasProperty(FluxControllerBlock.FACING)) return;
        Direction outward = controller.getValue(FluxControllerBlock.FACING);
        BlockPos center = FluxMultiblock.getCenter(controllerPos, outward);
        PoseStack pose = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        var buffers = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        FluxMultiblock.ValidationResult validation = FluxMultiblock.validateDetailed(minecraft.level, controllerPos);

        pose.pushPose();
        pose.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        for (FluxMultiblock.BlueprintCell cell : FluxMultiblock.blueprint(outward)) {
            if (cell.role() == FluxMultiblock.Role.AIR) continue;
            int cellLayer = cell.offset().getY() + FluxMultiblock.RADIUS;
            if (layer < 5 && cellLayer != layer) continue;
            BlockPos worldPos = center.offset(cell.offset());
            boolean wrong = validation.mismatches().stream().anyMatch(m -> m.pos().equals(worldPos));
            AABB box = new AABB(worldPos).inflate(wrong ? 0.012D : 0.004D);
            float pulse = 0.55F + 0.25F * (float) Math.sin((minecraft.level.getGameTime()
                    + event.getPartialTick()) * 0.15F);
            LevelRenderer.renderLineBox(pose, lines, box,
                    wrong ? 1.0F : 0.15F, wrong ? 0.18F : 0.85F, wrong ? 0.12F : 1.0F,
                    wrong ? 0.95F : pulse);
        }
        pose.popPose();
        buffers.endBatch(RenderType.lines());
    }
}
