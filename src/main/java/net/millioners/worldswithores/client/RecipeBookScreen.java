package net.millioners.worldswithores.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class RecipeBookScreen extends Screen {
    private static final int PANEL_W = 320;
    private static final int PANEL_H = 220;
    private static final int TAB_W = 88;

    private ModRecipePages.Category category = ModRecipePages.Category.GUIDE;
    private int pageIndex;
    private int left;
    private int top;

    public RecipeBookScreen() {
        super(Component.translatable("item.worlds_with_ores.recipes_book"));
    }

    @Override
    protected void init() {
        this.left = (this.width - PANEL_W) / 2;
        this.top = (this.height - PANEL_H) / 2;
        this.pageIndex = Math.min(this.pageIndex, Math.max(0, ModRecipePages.pagesFor(this.category).size() - 1));

        int bx = this.left + 8;
        int by = this.top + 28;
        for (ModRecipePages.Category cat : ModRecipePages.Category.values()) {
            ModRecipePages.Category captured = cat;
            this.addRenderableWidget(Button.builder(Component.translatable(cat.titleKey), b -> {
                this.category = captured;
                this.pageIndex = 0;
            }).bounds(bx, by, TAB_W, 18).build());
            by += 20;
        }

        int navY = this.top + PANEL_H - 28;
        int navX = this.left + TAB_W + 24;
        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
            if (!pages.isEmpty()) {
                this.pageIndex = (this.pageIndex - 1 + pages.size()) % pages.size();
            }
        }).bounds(navX, navY, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
            if (!pages.isEmpty()) {
                this.pageIndex = (this.pageIndex + 1) % pages.size();
            }
        }).bounds(navX + 120, navY, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> this.onClose())
                .bounds(this.left + PANEL_W - 58, this.top + 6, 50, 16).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.fill(this.left, this.top, this.left + PANEL_W, this.top + PANEL_H, 0xF0080808);
        graphics.fill(this.left + 1, this.top + 1, this.left + PANEL_W - 1, this.top + PANEL_H - 1, 0xFF1E1812);
        graphics.fill(this.left + TAB_W + 12, this.top + 24, this.left + PANEL_W - 8, this.top + PANEL_H - 36, 0xFF2A221A);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.top + 8, 0xFFE8DCC8);

        List<ModRecipePages.Page> pages = ModRecipePages.pagesFor(this.category);
        if (pages.isEmpty()) {
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        ModRecipePages.Page page = pages.get(Math.floorMod(this.pageIndex, pages.size()));
        int contentX = this.left + TAB_W + 20;
        graphics.drawString(this.font, page.title(), contentX, this.top + 30, 0xFFF0E6D2, false);

        switch (page.kind()) {
            case SMELTING -> renderSmelting(graphics, page, contentX, mouseX, mouseY);
            case INFO -> renderInfo(graphics, page, contentX, mouseX, mouseY);
            default -> renderCrafting(graphics, page, contentX, mouseX, mouseY);
        }

        if (!page.hint().getString().isEmpty() && page.kind() != ModRecipePages.Kind.INFO) {
            drawWrapped(graphics, page.hint(), contentX, this.top + 150, PANEL_W - TAB_W - 40, 0xFFC8B8A0);
        }

        String footer = (this.pageIndex + 1) + " / " + pages.size();
        graphics.drawCenteredString(this.font, footer, this.left + TAB_W + 90, this.top + PANEL_H - 22, 0xFFC8B8A0);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderCrafting(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int gridX = contentX;
        int gridY = this.top + 52;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = gridX + col * 20;
                int sy = gridY + row * 20;
                drawSlot(graphics, sx, sy);
                ItemStack stack = page.pattern().length > row * 3 + col ? page.pattern()[row * 3 + col] : ItemStack.EMPTY;
                if (!stack.isEmpty()) {
                    graphics.renderItem(stack, sx + 1, sy + 1);
                    graphics.renderItemDecorations(this.font, stack, sx + 1, sy + 1);
                }
            }
        }

        int rx = gridX + 82;
        int ry = gridY + 20;
        graphics.drawString(this.font, "->", rx - 16, ry + 4, 0xFFE8DCC8, false);
        drawSlot(graphics, rx, ry, 22);
        if (!page.result().isEmpty()) {
            graphics.renderItem(page.result(), rx + 3, ry + 3);
            graphics.renderItemDecorations(this.font, page.result(), rx + 3, ry + 3);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = gridX + col * 20;
                int sy = gridY + row * 20;
                ItemStack stack = page.pattern().length > row * 3 + col ? page.pattern()[row * 3 + col] : ItemStack.EMPTY;
                if (!stack.isEmpty() && mouseX >= sx && mouseX < sx + 18 && mouseY >= sy && mouseY < sy + 18) {
                    graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                }
            }
        }
        if (!page.result().isEmpty() && mouseX >= rx && mouseX < rx + 22 && mouseY >= ry && mouseY < ry + 22) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderSmelting(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int sx = contentX + 8;
        int sy = this.top + 70;
        ItemStack input = page.pattern().length > 0 ? page.pattern()[0] : ItemStack.EMPTY;
        ItemStack furnace = new ItemStack(Items.FURNACE);

        drawSlot(graphics, sx, sy, 22);
        if (!input.isEmpty()) {
            graphics.renderItem(input, sx + 3, sy + 3);
            graphics.renderItemDecorations(this.font, input, sx + 3, sy + 3);
        }

        graphics.drawString(this.font, "+", sx + 28, sy + 6, 0xFFE8DCC8, false);
        drawSlot(graphics, sx + 42, sy, 22);
        graphics.renderItem(furnace, sx + 45, sy + 3);

        graphics.drawString(this.font, "->", sx + 70, sy + 6, 0xFFE8DCC8, false);
        drawSlot(graphics, sx + 90, sy, 22);
        graphics.renderItem(page.result(), sx + 93, sy + 3);
        graphics.renderItemDecorations(this.font, page.result(), sx + 93, sy + 3);

        if (!input.isEmpty() && mouseX >= sx && mouseX < sx + 22 && mouseY >= sy && mouseY < sy + 22) {
            graphics.renderTooltip(this.font, input, mouseX, mouseY);
        }
        if (mouseX >= sx + 42 && mouseX < sx + 64 && mouseY >= sy && mouseY < sy + 22) {
            graphics.renderTooltip(this.font, furnace, mouseX, mouseY);
        }
        if (!page.result().isEmpty() && mouseX >= sx + 90 && mouseX < sx + 112 && mouseY >= sy && mouseY < sy + 22) {
            graphics.renderTooltip(this.font, page.result(), mouseX, mouseY);
        }
    }

    private void renderInfo(GuiGraphics graphics, ModRecipePages.Page page, int contentX, int mouseX, int mouseY) {
        int ix = contentX;
        int iy = this.top + 52;
        for (int n = 0; n < page.pattern().length; n++) {
            ItemStack stack = page.pattern()[n];
            if (stack.isEmpty()) {
                continue;
            }
            int sx = ix + n * 24;
            drawSlot(graphics, sx, iy, 22);
            graphics.renderItem(stack, sx + 3, iy + 3);
            graphics.renderItemDecorations(this.font, stack, sx + 3, iy + 3);
            if (mouseX >= sx && mouseX < sx + 22 && mouseY >= iy && mouseY < iy + 22) {
                graphics.renderTooltip(this.font, stack, mouseX, mouseY);
            }
        }
        drawWrapped(graphics, page.hint(), contentX, iy + 34, PANEL_W - TAB_W - 40, 0xFFE8DCC8);
    }

    private void drawWrapped(GuiGraphics graphics, Component text, int x, int y, int width, int color) {
        List<FormattedCharSequence> lines = this.font.split(text, width);
        int yy = y;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(this.font, line, x, yy, color, false);
            yy += 10;
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        drawSlot(graphics, x, y, 18);
    }

    private void drawSlot(GuiGraphics graphics, int x, int y, int size) {
        graphics.fill(x, y, x + size, y + size, 0xFF000000);
        graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xFF8B7355);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
