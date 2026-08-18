package net.millioners.worldswithores.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.millioners.worldswithores.menu.FluxControllerMenu;

public class FluxControllerScreen extends AbstractContainerScreen<FluxControllerMenu> {
    private static final int COL_BG = 0xFF121820;
    private static final int COL_PANEL = 0xFF1A222A;
    private static final int COL_ACCENT = 0xFF6FA8C8;
    private static final int COL_ENERGY = 0xFF3DDC97;
    private static final int COL_BURN = 0xFFFFB347;
    private static final int COL_TEXT = 0xFFE8EEF2;
    private static final int COL_MUTED = 0xFF93A3B0;
    private static final int COL_BAD = 0xFFE07070;
    private static final int COL_GOOD = 0xFF6FCF97;

    public FluxControllerScreen(FluxControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 196;
        this.imageHeight = 174;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x - 2, y - 2, x + this.imageWidth + 2, y + this.imageHeight + 2, 0xFF2E3C48);
        graphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, COL_BG);
        graphics.fill(x + 1, y + 1, x + this.imageWidth - 1, y + 22, COL_PANEL);
        graphics.fill(x + 1, y + 22, x + this.imageWidth - 1, y + 23, COL_ACCENT);

        // fuel well
        graphics.fill(x + 36, y + 34, x + 68, y + 66, 0xFF0A0E12);
        graphics.fill(x + 38, y + 36, x + 66, y + 64, 0xFF243040);

        // energy bar background
        graphics.fill(x + 84, y + 36, x + 176, y + 52, 0xFF0A0E12);
        int energy = this.menu.getData().get(0);
        int capacity = Math.max(1, this.menu.getData().get(1));
        int energyW = (int) (90.0F * energy / (float) capacity);
        if (energyW > 0) {
            graphics.fill(x + 85, y + 37, x + 85 + energyW, y + 51, COL_ENERGY);
        }

        // burn bar
        graphics.fill(x + 84, y + 58, x + 176, y + 68, 0xFF0A0E12);
        int burnLeft = this.menu.getData().get(2);
        int burnTotal = Math.max(1, this.menu.getData().get(3));
        int burnW = this.menu.getData().get(3) <= 0 ? 0 : (int) (90.0F * burnLeft / (float) burnTotal);
        if (burnW > 0) {
            graphics.fill(x + 85, y + 59, x + 85 + burnW, y + 67, COL_BURN);
        }

        // status chip
        boolean formed = this.menu.getData().get(4) == 1;
        graphics.fill(x + 84, y + 74, x + 176, y + 88, formed ? 0xFF1A3A2A : 0xFF3A1A1A);
        graphics.drawCenteredString(this.font,
                Component.translatable(formed ? "gui.worlds_with_ores.flux.formed" : "gui.worlds_with_ores.flux.incomplete"),
                x + 130, y + 77, formed ? COL_GOOD : COL_BAD);

        // player inventory frame
        graphics.fill(x + 8, y + 90, x + this.imageWidth - 8, y + this.imageHeight - 6, COL_PANEL);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 8, COL_TEXT, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.fuel"), 36, 24, COL_MUTED, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.energy"), 84, 24, COL_MUTED, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        int x = this.leftPos;
        int y = this.topPos;
        if (mouseX >= x + 84 && mouseX < x + 176 && mouseY >= y + 36 && mouseY < y + 52) {
            graphics.renderTooltip(this.font,
                    Component.translatable("gui.worlds_with_ores.flux.energy_tooltip",
                            this.menu.getData().get(0), this.menu.getData().get(1)),
                    mouseX, mouseY);
        }
    }
}
