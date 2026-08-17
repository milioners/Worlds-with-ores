package net.millioners.worldswithores.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;

import java.util.List;

public class RecipeBookScreen extends Screen {
    private static final int PANEL_W = 360;
    private static final int PANEL_H = 248;
    private static final int TAB_W = 92;

    private ModRecipePages.Category category = ModRecipePages.Category.GUIDE;
    private int pageIndex;
    private int left;
    private int top;
    private float portalSpin;

    public RecipeBookScreen() {
        super(Component.translatable("item.worlds_with_ores.recipes_book"));
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.left = (this.width - PANEL_W) / 2;
        this.top = (this.height - PANEL_H) / 2;
        this.pageIndex = Math.min(this.pageIndex, Math.max(0, ModRecipePages.pagesFor(this.category).size() - 1));

        int bx = this.left + 8;
        int by = this.top + 30;
        for (ModRecipePages.Category cat : ModRecipePages.Category.values()) {
            ModRecipePages.Category captured = cat;
            boolean selected = this.category == cat;
            this.addRenderableWidget(Button.builder(Component.translatable(cat.titleKey), b -> {
                this.category = captured;
                this.pageIndex = 0;
                this.init();
            }).bounds(bx, by, TAB_W, 16).build());
            by += 18;
            if (selected) {
                // visual handled in render
            }
        }

        int navY = this.top + PANEL_H - 28;
        int navX = this.left + TAB_W + 36;
        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
            if (!pages.isEmpty()) {
                this.pageIndex = (this.pageIndex - 1 + pages.size()) % pages.size();
            }
        }).bounds(navX, navY, 22, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
            if (!pages.isEmpty()) {
                this.pageIndex = (this.pageIndex + 1) % pages.size();
            }
        }).bounds(navX + 148, navY, 22, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> this.onClose())
                .bounds(this.left + PANEL_W - 58, this.top + 6, 50, 16).build());
    }

    @Override
    public void tick() {
        this.portalSpin += 1.6F;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        // Outer frame + parchment
        graphics.fill(this.left - 2, this.top - 2, this.left + PANEL_W + 2, this.top + PANEL_H + 2, 0xFF2C2118);
        graphics.fill(this.left, this.top, this.left + PANEL_W, this.top + PANEL_H, 0xFF3A2C20);
        graphics.fill(this.left + 3, this.top + 3, this.left + PANEL_W - 3, this.top + PANEL_H - 3, 0xFFD8C7A4);
        graphics.fill(this.left + 5, this.top + 5, this.left + PANEL_W - 5, this.top + PANEL_H - 5, 0xFFCDB792);

        // Side rail
        graphics.fill(this.left + 6, this.top + 24, this.left + TAB_W + 14, this.top + PANEL_H - 8, 0xFFB89E74);
        // Content page
        graphics.fill(this.left + TAB_W + 18, this.top + 24, this.left + PANEL_W - 8, this.top + PANEL_H - 34, 0xFFE8D9B8);
        graphics.fill(this.left + TAB_W + 19, this.top + 25, this.left + PANEL_W - 9, this.top + PANEL_H - 35, 0xFFF2E6C8);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.top + 8, 0xFF3A2A18);

        // Highlight selected tab
        int by = this.top + 30;
        for (ModRecipePages.Category cat : ModRecipePages.Category.values()) {
            if (cat == this.category) {
                graphics.fill(this.left + 6, by - 1, this.left + TAB_W + 12, by + 17, 0x664A3820);
            }
            by += 18;
        }

        List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
        if (pages.isEmpty()) {
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        ModRecipePages.Page page = pages.get(Math.floorMod(this.pageIndex, pages.size()));
        int contentX = this.left + TAB_W + 28;
        graphics.drawString(this.font, page.title(), contentX, this.top + 32, 0xFF3A2A18, false);

        switch (page.kind()) {
            case SMELTING -> renderSmelting(graphics, page, contentX, mouseX, mouseY);
            case INFO -> renderInfo(graphics, page, contentX, mouseX, mouseY);
            case PORTAL -> renderPortal(graphics, page, contentX, mouseX, mouseY, partialTick);
            default -> renderCrafting(graphics, page, contentX, mouseX, mouseY);
        }

        if (!page.hint().getString().isEmpty() && page.kind() != ModRecipePages.Kind.INFO) {
            int hintY = page.kind() == ModRecipePages.Kind.PORTAL ? this.top + 188 : this.top + 168;
            drawWrapped(graphics, page.hint(), contentX, hintY, PANEL_W - TAB_W - 48, 0xFF5A4630);
        }

        String footer = (this.pageIndex + 1) + " / " + pages.size();
        graphics.drawCenteredString(this.font, footer, this.left + TAB_W + 118, this.top + PANEL_H - 22, 0xFF5A4630);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderPortal(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY, float partialTick) {
        int stageX = contentX + 8;
        int stageY = this.top + 58;
        int stageW = 120;
        int stageH = 118;

        // Soft stage for the 3D portal model
        graphics.fill(stageX, stageY, stageX + stageW, stageY + stageH, 0xFF2A221A);
        graphics.fill(stageX + 2, stageY + 2, stageX + stageW - 2, stageY + stageH - 2, 0xFF17120E);
        graphics.fill(stageX + 4, stageY + stageH - 18, stageX + stageW - 4, stageY + stageH - 6, 0x3322AA88);

        if (!page.portalPreview().isEmpty()) {
            float spin = this.portalSpin + partialTick * 1.6F;
            renderPreviewModel(graphics, page.portalPreview(), stageX + stageW / 2.0F, stageY + 58.0F, 56.0F, spin);
        }

        int rx = contentX + 148;
        int ry = this.top + 78;
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.book.portal.frame_block"),
                rx, this.top + 52, 0xFF5A4630, false);
        drawSlot(graphics, rx, this.top + 64, 26);
        if (!page.frameBlock().isEmpty()) {
            graphics.renderItem(page.frameBlock(), rx + 5, this.top + 69);
            graphics.renderItemDecorations(this.font, page.frameBlock(), rx + 5, this.top + 69);
        }

        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.book.portal.need_igniter"),
                rx, ry + 30, 0xFF5A4630, false);
        drawSlot(graphics, rx, ry + 42, 26);
        if (!page.result().isEmpty()) {
            graphics.renderItem(page.result(), rx + 5, ry + 47);
            graphics.renderItemDecorations(this.font, page.result(), rx + 5, ry + 47);
        }

        if (!page.frameBlock().isEmpty() && mouseX >= rx && mouseX < rx + 26 && mouseY >= this.top + 64 && mouseY < this.top + 90) {
            graphics.renderTooltip(this.font, page.frameBlock(), mouseX, mouseY);
        }
        if (!page.result().isEmpty() && mouseX >= rx && mouseX < rx + 26 && mouseY >= ry + 42 && mouseY < ry + 68) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderPreviewModel(GuiGraphics graphics, ItemStack stack, float x, float y, float scale, float spinDeg) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 150.0F);
        pose.scale(scale, -scale, scale);
        pose.mulPose(new Quaternionf().rotationXYZ(
                (float) Math.toRadians(22.0F),
                (float) Math.toRadians(spinDeg),
                0.0F
        ));
        // gentle bob
        pose.translate(0.0F, Mth.sin(spinDeg * 0.04F) * 0.05F, 0.0F);

        Lighting.setupForFlatItems();
        RenderSystem.assertOnRenderThread();
        var buffers = this.minecraft.renderBuffers().bufferSource();
        this.minecraft.getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.GUI,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                pose,
                buffers,
                this.minecraft.level,
                0
        );
        buffers.endBatch();
        Lighting.setupFor3DItems();
        pose.popPose();
    }

    private void renderCrafting(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int gridX = contentX;
        int gridY = this.top + 56;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = gridX + col * 22;
                int sy = gridY + row * 22;
                drawSlot(graphics, sx, sy, 20);
                ItemStack stack = page.pattern().length > row * 3 + col ? page.pattern()[row * 3 + col] : ItemStack.EMPTY;
                if (!stack.isEmpty()) {
                    graphics.renderItem(stack, sx + 2, sy + 2);
                    graphics.renderItemDecorations(this.font, stack, sx + 2, sy + 2);
                }
            }
        }

        int rx = gridX + 96;
        int ry = gridY + 22;
        graphics.drawString(this.font, "->", rx - 18, ry + 5, 0xFF3A2A18, false);
        drawSlot(graphics, rx, ry, 24);
        if (!page.result().isEmpty()) {
            graphics.renderItem(page.result(), rx + 4, ry + 4);
            graphics.renderItemDecorations(this.font, page.result(), rx + 4, ry + 4);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = gridX + col * 22;
                int sy = gridY + row * 22;
                ItemStack stack = page.pattern().length > row * 3 + col ? page.pattern()[row * 3 + col] : ItemStack.EMPTY;
                if (!stack.isEmpty() && mouseX >= sx && mouseX < sx + 20 && mouseY >= sy && mouseY < sy + 20) {
                    graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                }
            }
        }
        if (!page.result().isEmpty() && mouseX >= rx && mouseX < rx + 24 && mouseY >= ry && mouseY < ry + 24) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderSmelting(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int sx = contentX + 8;
        int sy = this.top + 80;
        ItemStack input = page.pattern().length > 0 ? page.pattern()[0] : ItemStack.EMPTY;
        ItemStack furnace = new ItemStack(Items.FURNACE);

        drawSlot(graphics, sx, sy, 24);
        if (!input.isEmpty()) {
            graphics.renderItem(input, sx + 4, sy + 4);
            graphics.renderItemDecorations(this.font, input, sx + 4, sy + 4);
        }

        graphics.drawString(this.font, "+", sx + 32, sy + 8, 0xFF3A2A18, false);
        drawSlot(graphics, sx + 48, sy, 24);
        graphics.renderItem(furnace, sx + 52, sy + 4);

        graphics.drawString(this.font, "->", sx + 80, sy + 8, 0xFF3A2A18, false);
        drawSlot(graphics, sx + 102, sy, 24);
        graphics.renderItem(page.result(), sx + 106, sy + 4);
        graphics.renderItemDecorations(this.font, page.result(), sx + 106, sy + 4);

        if (!input.isEmpty() && mouseX >= sx && mouseX < sx + 24 && mouseY >= sy && mouseY < sy + 24) {
            graphics.renderTooltip(this.font, input, mouseX, mouseY);
        }
        if (mouseX >= sx + 48 && mouseX < sx + 72 && mouseY >= sy && mouseY < sy + 24) {
            graphics.renderTooltip(this.font, furnace, mouseX, mouseY);
        }
        if (!page.result().isEmpty() && mouseX >= sx + 102 && mouseX < sx + 126 && mouseY >= sy && mouseY < sy + 24) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderInfo(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int ix = contentX;
        int iy = this.top + 56;
        for (int n = 0; n < page.pattern().length; n++) {
            ItemStack stack = page.pattern()[n];
            if (stack.isEmpty()) {
                continue;
            }
            int sx = ix + n * 26;
            drawSlot(graphics, sx, iy, 24);
            graphics.renderItem(stack, sx + 4, iy + 4);
            graphics.renderItemDecorations(this.font, stack, sx + 4, iy + 4);
            if (mouseX >= sx && mouseX < sx + 24 && mouseY >= iy && mouseY < iy + 24) {
                graphics.renderTooltip(this.font, stack, mouseX, mouseY);
            }
        }
        drawWrapped(graphics, page.hint(), contentX, iy + 38, PANEL_W - TAB_W - 48, 0xFF3A2A18);
    }

    private void drawWrapped(GuiGraphics graphics, Component text, int x, int y, int width, int color) {
        List<FormattedCharSequence> lines = this.font.split(text, width);
        int yy = y;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(this.font, line, x, yy, color, false);
            yy += 10;
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y, int size) {
        graphics.fill(x, y, x + size, y + size, 0xFF2C2118);
        graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xFF8B7355);
        graphics.fill(x + 2, y + 2, x + size - 2, y + size - 2, 0xFFA88860);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
