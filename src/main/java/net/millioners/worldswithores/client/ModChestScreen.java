package net.millioners.worldswithores.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.millioners.worldswithores.menu.ModChestMenu;

public class ModChestScreen extends AbstractContainerScreen<ModChestMenu> {
    private static final int COL_BG = 0xFF10171D;
    private static final int COL_PANEL = 0xFF18232C;
    private static final int COL_ACCENT = 0xFF43E8FF;
    private static final int COL_TEXT = 0xFFE8EEF2;
    private static final int COL_MUTED = 0xFF93A3B0;

    public ModChestScreen(ModChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 250;
        this.imageHeight = 182;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("gui.worlds_with_ores.chest.sort"),
                        button -> sendButton(0))
                .bounds(leftPos + 168, topPos + 4, 36, 14).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.worlds_with_ores.chest.deposit"),
                        button -> sendButton(1))
                .bounds(leftPos + 206, topPos + 4, 40, 14).build());
    }

    private void sendButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.chest.title"), 8, 6, COL_TEXT, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 45, 88, COL_MUTED, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x - 2, y - 2, x + imageWidth + 2, y + imageHeight + 2, 0xFF2E3C48);
        graphics.fill(x, y, x + imageWidth, y + imageHeight, COL_BG);
        graphics.fill(x + 1, y + 1, x + imageWidth - 1, y + 20, COL_PANEL);
        graphics.fill(x + 1, y + 20, x + imageWidth - 1, y + 22, COL_ACCENT);
        graphics.fill(x + 8, y + 24, x + imageWidth - 8, y + 82, COL_PANEL);
        graphics.fill(x + 36, y + 94, x + imageWidth - 36, y + imageHeight - 5, COL_PANEL);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 11; col++) {
                drawSlot(graphics, x + 26 + col * 18, y + 24 + row * 18);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlot(graphics, x + 45 + col * 18, y + 100 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlot(graphics, x + 45 + col * 18, y + 158);
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 2, y - 2, x + 18, y + 18, 0xFF070A0D);
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF324252);
        graphics.fill(x, y, x + 16, y + 16, 0xFF0A0E12);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
