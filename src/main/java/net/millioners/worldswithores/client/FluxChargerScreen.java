package net.millioners.worldswithores.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.millioners.worldswithores.menu.FluxChargerMenu;

public class FluxChargerScreen extends AbstractContainerScreen<FluxChargerMenu> {
    public FluxChargerScreen(FluxChargerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 196;
        this.imageHeight = 174;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x - 2, y - 2, x + this.imageWidth + 2, y + this.imageHeight + 2, 0xFF2E3C48);
        graphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF121820);
        graphics.fill(x + 1, y + 1, x + this.imageWidth - 1, y + 22, 0xFF1A222A);
        graphics.fill(x + 1, y + 22, x + this.imageWidth - 1, y + 23, 0xFF6FA8C8);

        graphics.fill(x + 72, y + 32, x + 104, y + 64, 0xFF0A0E12);
        graphics.fill(x + 74, y + 34, x + 102, y + 62, 0xFF243040);

        graphics.fill(x + 28, y + 70, x + 168, y + 84, 0xFF0A0E12);
        int energy = this.menu.getData().get(0);
        int capacity = Math.max(1, this.menu.getData().get(1));
        int w = (int) (138.0F * energy / (float) capacity);
        if (w > 0) {
            graphics.fill(x + 29, y + 71, x + 29 + w, y + 83, 0xFF6FA8C8);
        }
        graphics.fill(x + 8, y + 90, x + this.imageWidth - 8, y + this.imageHeight - 6, 0xFF1A222A);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 8, 0xFFE8EEF2, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.charge_slot"), 68, 22, 0xFF93A3B0, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;
        if (mouseX >= x + 28 && mouseX < x + 168 && mouseY >= y + 70 && mouseY < y + 84) {
            graphics.renderTooltip(this.font,
                    Component.translatable("gui.worlds_with_ores.flux.energy_tooltip",
                            this.menu.getData().get(0), this.menu.getData().get(1)),
                    mouseX, mouseY);
        }
    }
}
