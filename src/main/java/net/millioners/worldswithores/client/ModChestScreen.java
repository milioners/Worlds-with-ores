package net.millioners.worldswithores.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.millioners.worldswithores.menu.ModChestMenu;

public class ModChestScreen extends AbstractContainerScreen<ModChestMenu> {
    public ModChestScreen(ModChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 250;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = 8;
        this.titleLabelY = 61;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 26, 61, 0xFFE8EEF2, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 45, 73, 0xFF93A3B0, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x - 2, y - 2, x + imageWidth + 2, y + imageHeight + 2, 0xFF2E3C48);
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF10171D);
        graphics.fill(x + 5, y + 4, x + imageWidth - 5, y + 62, 0xFF18232C);
        graphics.fill(x + 5, y + 62, x + imageWidth - 5, y + 64, 0xFF43E8FF);
        graphics.fill(x + 36, y + 78, x + imageWidth - 36, y + imageHeight - 5, 0xFF18232C);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 11; col++) drawSlot(graphics, x + 26 + col * 18, y + 8 + row * 18);
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) drawSlot(graphics, x + 45 + col * 18, y + 84 + row * 18);
        }
        for (int col = 0; col < 9; col++) drawSlot(graphics, x + 45 + col * 18, y + 142);
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
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
