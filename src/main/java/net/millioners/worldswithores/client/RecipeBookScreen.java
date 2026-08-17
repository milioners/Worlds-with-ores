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
    private static final int PANEL_W = 372;
    private static final int PANEL_H = 240;
    private static final int TAB_W = 86;

    private static final int COL_OUTER = 0xFF2E3C48;
    private static final int COL_BG = 0xFF121820;
    private static final int COL_SIDE = 0xFF0E141A;
    private static final int COL_PAGE = 0xFF182028;
    private static final int COL_ACCENT = 0xFF6FA8C8;
    private static final int COL_TEXT = 0xFFE8EEF2;
    private static final int COL_MUTED = 0xFF93A3B0;

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
            this.addRenderableWidget(Button.builder(Component.translatable(cat.titleKey), b -> {
                this.category = captured;
                this.pageIndex = 0;
                this.init();
            }).bounds(bx, by, TAB_W, 18).build());
            by += 20;
        }

        int navY = this.top + PANEL_H - 26;
        int navX = this.left + TAB_W + 40;
        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
            if (!pages.isEmpty()) {
                this.pageIndex = (this.pageIndex - 1 + pages.size()) % pages.size();
            }
        }).bounds(navX, navY, 22, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
            if (!pages.isEmpty()) {
                this.pageIndex = (this.pageIndex + 1) % pages.size();
            }
        }).bounds(navX + 140, navY, 22, 18).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> this.onClose())
                .bounds(this.left + PANEL_W - 54, this.top + 5, 48, 16).build());
    }

    @Override
    public void tick() {
        this.portalSpin += 1.8F;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.fill(this.left - 2, this.top - 2, this.left + PANEL_W + 2, this.top + PANEL_H + 2, COL_OUTER);
        graphics.fill(this.left, this.top, this.left + PANEL_W, this.top + PANEL_H, COL_BG);
        graphics.fill(this.left + 1, this.top + 1, this.left + PANEL_W - 1, this.top + 23, COL_SIDE);
        graphics.fill(this.left + 1, this.top + 23, this.left + PANEL_W - 1, this.top + 24, COL_ACCENT);

        graphics.fill(this.left + 6, this.top + 28, this.left + TAB_W + 14, this.top + PANEL_H - 8, COL_SIDE);
        graphics.fill(this.left + TAB_W + 18, this.top + 28, this.left + PANEL_W - 6, this.top + PANEL_H - 30, COL_PAGE);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.top + 8, COL_TEXT);

        int by = this.top + 30;
        for (ModRecipePages.Category cat : ModRecipePages.Category.values()) {
            if (cat == this.category) {
                graphics.fill(this.left + 6, by - 1, this.left + 9, by + 19, COL_ACCENT);
            }
            by += 20;
        }

        List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
        if (pages.isEmpty()) {
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        ModRecipePages.Page page = pages.get(Math.floorMod(this.pageIndex, pages.size()));
        int contentX = this.left + TAB_W + 28;
        graphics.drawString(this.font, page.title(), contentX, this.top + 36, COL_ACCENT, false);

        switch (page.kind()) {
            case SMELTING -> renderSmelting(graphics, page, contentX, mouseX, mouseY);
            case INFO -> renderInfo(graphics, page, contentX, mouseX, mouseY);
            case PORTAL -> renderPortal(graphics, page, contentX, mouseX, mouseY, partialTick);
            default -> renderCrafting(graphics, page, contentX, mouseX, mouseY);
        }

        if (!page.hint().getString().isEmpty() && page.kind() != ModRecipePages.Kind.INFO) {
            int hintY = page.kind() == ModRecipePages.Kind.PORTAL ? this.top + 178 : this.top + 168;
            drawWrapped(graphics, page.hint(), contentX, hintY, PANEL_W - TAB_W - 44, COL_MUTED);
        }

        String footer = (this.pageIndex + 1) + " / " + pages.size();
        graphics.drawCenteredString(this.font, footer, this.left + TAB_W + 110, this.top + PANEL_H - 22, COL_MUTED);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderPortal(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY, float partialTick) {
        // Floating 3D portal — no stage/background box
        if (!page.portalPreview().isEmpty()) {
            float spin = this.portalSpin + partialTick * 1.8F;
            renderPreviewModel(graphics, page.portalPreview(), contentX + 62.0F, this.top + 120.0F, 64.0F, spin);
        }

        int rx = contentX + 145;
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.book.portal.frame_block"),
                rx, this.top + 64, COL_MUTED, false);
        drawSlot(graphics, rx, this.top + 76, 28);
        if (!page.frameBlock().isEmpty()) {
            graphics.renderItem(page.frameBlock(), rx + 6, this.top + 82);
            graphics.renderItemDecorations(this.font, page.frameBlock(), rx + 6, this.top + 82);
        }

        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.book.portal.need_igniter"),
                rx, this.top + 116, COL_MUTED, false);
        drawSlot(graphics, rx, this.top + 128, 28);
        if (!page.result().isEmpty()) {
            graphics.renderItem(page.result(), rx + 6, this.top + 134);
            graphics.renderItemDecorations(this.font, page.result(), rx + 6, this.top + 134);
        }

        if (!page.frameBlock().isEmpty() && mouseX >= rx && mouseX < rx + 28 && mouseY >= this.top + 76 && mouseY < this.top + 104) {
            graphics.renderTooltip(this.font, page.frameBlock(), mouseX, mouseY);
        }
        if (!page.result().isEmpty() && mouseX >= rx && mouseX < rx + 28 && mouseY >= this.top + 128 && mouseY < this.top + 156) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderPreviewModel(GuiGraphics graphics, ItemStack stack, float x, float y, float scale, float spinDeg) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 200.0F);
        pose.scale(scale, -scale, scale);
        pose.mulPose(new Quaternionf().rotationXYZ(
                (float) Math.toRadians(25.0F),
                (float) Math.toRadians(spinDeg),
                0.0F
        ));
        pose.translate(0.0F, Mth.sin(spinDeg * 0.045F) * 0.04F, 0.0F);

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
        int gridY = this.top + 70;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = gridX + col * 24;
                int sy = gridY + row * 24;
                drawSlot(graphics, sx, sy, 22);
                ItemStack stack = page.pattern().length > row * 3 + col ? page.pattern()[row * 3 + col] : ItemStack.EMPTY;
                if (!stack.isEmpty()) {
                    graphics.renderItem(stack, sx + 3, sy + 3);
                    graphics.renderItemDecorations(this.font, stack, sx + 3, sy + 3);
                }
            }
        }

        int rx = gridX + 108;
        int ry = gridY + 24;
        graphics.drawString(this.font, "->", rx - 20, ry + 6, COL_ACCENT, false);
        drawSlot(graphics, rx, ry, 26);
        if (!page.result().isEmpty()) {
            graphics.renderItem(page.result(), rx + 5, ry + 5);
            graphics.renderItemDecorations(this.font, page.result(), rx + 5, ry + 5);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = gridX + col * 24;
                int sy = gridY + row * 24;
                ItemStack stack = page.pattern().length > row * 3 + col ? page.pattern()[row * 3 + col] : ItemStack.EMPTY;
                if (!stack.isEmpty() && mouseX >= sx && mouseX < sx + 22 && mouseY >= sy && mouseY < sy + 22) {
                    graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                }
            }
        }
        if (!page.result().isEmpty() && mouseX >= rx && mouseX < rx + 26 && mouseY >= ry && mouseY < ry + 26) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderSmelting(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int sx = contentX + 8;
        int sy = this.top + 92;
        ItemStack input = page.pattern().length > 0 ? page.pattern()[0] : ItemStack.EMPTY;
        ItemStack furnace = new ItemStack(Items.FURNACE);

        drawSlot(graphics, sx, sy, 26);
        if (!input.isEmpty()) {
            graphics.renderItem(input, sx + 5, sy + 5);
            graphics.renderItemDecorations(this.font, input, sx + 5, sy + 5);
        }

        graphics.drawString(this.font, "+", sx + 36, sy + 8, COL_ACCENT, false);
        drawSlot(graphics, sx + 52, sy, 26);
        graphics.renderItem(furnace, sx + 57, sy + 5);

        graphics.drawString(this.font, "->", sx + 88, sy + 8, COL_ACCENT, false);
        drawSlot(graphics, sx + 110, sy, 26);
        graphics.renderItem(page.result(), sx + 115, sy + 5);
        graphics.renderItemDecorations(this.font, page.result(), sx + 115, sy + 5);

        if (!input.isEmpty() && mouseX >= sx && mouseX < sx + 26 && mouseY >= sy && mouseY < sy + 26) {
            graphics.renderTooltip(this.font, input, mouseX, mouseY);
        }
        if (mouseX >= sx + 52 && mouseX < sx + 78 && mouseY >= sy && mouseY < sy + 26) {
            graphics.renderTooltip(this.font, furnace, mouseX, mouseY);
        }
        if (!page.result().isEmpty() && mouseX >= sx + 110 && mouseX < sx + 136 && mouseY >= sy && mouseY < sy + 26) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderInfo(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int ix = contentX;
        int iy = this.top + 70;
        for (int n = 0; n < page.pattern().length; n++) {
            ItemStack stack = page.pattern()[n];
            if (stack.isEmpty()) {
                continue;
            }
            int sx = ix + n * 28;
            drawSlot(graphics, sx, iy, 26);
            graphics.renderItem(stack, sx + 5, iy + 5);
            graphics.renderItemDecorations(this.font, stack, sx + 5, iy + 5);
            if (mouseX >= sx && mouseX < sx + 26 && mouseY >= iy && mouseY < iy + 26) {
                graphics.renderTooltip(this.font, stack, mouseX, mouseY);
            }
        }
        drawWrapped(graphics, page.hint(), contentX, iy + 40, PANEL_W - TAB_W - 44, COL_TEXT);
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
        graphics.fill(x, y, x + size, y + size, COL_OUTER);
        graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xFF0A0E12);
        graphics.fill(x + 2, y + 2, x + size - 2, y + size - 2, 0xFF243040);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
