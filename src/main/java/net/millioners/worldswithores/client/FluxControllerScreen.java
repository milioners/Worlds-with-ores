package net.millioners.worldswithores.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
    private final int[] energyHistory = new int[28];
    private final int[] heatHistory = new int[28];
    private long lastSample;
    private Button hologramButton;
    private Button autoButton;

    public FluxControllerScreen(FluxControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 248;
        this.imageHeight = 250;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        for (int index = 0; index < 4; index++) {
            final int buttonId = index;
            addRenderableWidget(Button.builder(Component.literal((index + 1) * 25 + "%"),
                            button -> sendMenuButton(buttonId))
                    .bounds(leftPos + 90 + index * 28, topPos + 132, 26, 14).build());
        }
        this.autoButton = addRenderableWidget(Button.builder(Component.empty(), button -> sendMenuButton(4))
                .bounds(leftPos + 204, topPos + 132, 36, 14).build());
        this.hologramButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
                    FluxStructureOverlay.cycle(this.menu.getController().getBlockPos());
                    updateButtonLabels();
                }).bounds(leftPos + 8, topPos + 132, 76, 14).build());
        updateButtonLabels();
    }

    private void sendMenuButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    private void updateButtonLabels() {
        if (hologramButton != null) {
            int mode = FluxStructureOverlay.mode();
            hologramButton.setMessage(Component.translatable(mode < 0
                    ? "gui.worlds_with_ores.flux.hologram.off"
                    : mode == 5 ? "gui.worlds_with_ores.flux.hologram.all"
                    : "gui.worlds_with_ores.flux.hologram.layer", mode + 1));
        }
        if (autoButton != null) {
            autoButton.setMessage(Component.translatable(this.menu.getData().get(17) == 1
                    ? "gui.worlds_with_ores.flux.auto.on" : "gui.worlds_with_ores.flux.auto.off"));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x - 2, y - 2, x + this.imageWidth + 2, y + this.imageHeight + 2, 0xFF2E3C48);
        graphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, COL_BG);
        graphics.fill(x + 1, y + 1, x + this.imageWidth - 1, y + 22, COL_PANEL);
        graphics.fill(x + 1, y + 22, x + this.imageWidth - 1, y + 24, COL_ACCENT);

        // Left machine chamber (fuel / coolant only).
        graphics.fill(x + 8, y + 28, x + 84, y + 100, 0xFF0A0E12);
        graphics.fill(x + 10, y + 30, x + 82, y + 98, 0xFF141E28);
        drawSlotWell(graphics, x + 26, y + 40);
        drawSlotWell(graphics, x + 52, y + 40);

        float time = (System.currentTimeMillis() % 10000L) / 1000.0F;
        float pulse = 0.5F + 0.5F * Mth.sin(time * 4.0F);
        int core = this.menu.getData().get(9) == 1 ? 0xFFFF684F : 0xFF43E8FF;
        int glow = this.menu.getData().get(9) == 1 ? 0x44FF3C28 : 0x443DDCFF;
        int cx = x + 46;
        int cy = y + 78;
        int radius = 10 + (int) (pulse * 2.0F);
        graphics.fill(cx - radius, cy - 1, cx + radius + 1, cy + 2, glow);
        graphics.fill(cx - 1, cy - radius, cx + 2, cy + radius + 1, glow);
        graphics.fill(cx - 6, cy - 6, cx + 7, cy + 7, 0xFF203747);
        graphics.fill(cx - 4, cy - 4, cx + 5, cy + 5, core);
        graphics.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFFFFFF);

        // Module row under the chamber.
        graphics.fill(x + 8, y + 102, x + 84, y + 128, COL_PANEL);
        for (int i = 0; i < 4; i++) {
            drawSlotWell(graphics, x + 10 + i * 20, y + 108);
        }

        // Right telemetry panel.
        graphics.fill(x + 88, y + 28, x + 240, y + 128, COL_PANEL);

        graphics.fill(x + 94, y + 42, x + 234, y + 54, 0xFF0A0E12);
        int energy = this.menu.getData().get(0);
        int capacity = Math.max(1, this.menu.getData().get(1));
        int energyW = (int) (138.0F * energy / (float) capacity);
        if (energyW > 0) {
            graphics.fill(x + 95, y + 43, x + 95 + energyW, y + 53, COL_ENERGY);
        }

        graphics.fill(x + 94, y + 64, x + 234, y + 74, 0xFF0A0E12);
        int heat = this.menu.getData().get(5);
        int maxHeat = Math.max(1, this.menu.getData().get(6));
        int heatW = (int) (138.0F * heat / (float) maxHeat);
        int heatColor = heat > 800 ? COL_BAD : heat > 500 ? COL_BURN : COL_ACCENT;
        if (heatW > 0) {
            graphics.fill(x + 95, y + 65, x + 95 + heatW, y + 73, heatColor);
        }

        sampleHistory(energy * 100 / capacity, heat * 100 / maxHeat);
        drawGraph(graphics, x + 94, y + 80, energyHistory, COL_ENERGY);
        drawGraph(graphics, x + 163, y + 80, heatHistory, heatColor);

        graphics.fill(x + 94, y + 102, x + 160, y + 110, 0xFF0A0E12);
        int coolantW = (int) (64.0F * this.menu.getData().get(10)
                / (float) net.millioners.worldswithores.blockentity.FluxControllerBlockEntity.COOLANT_DURATION);
        if (coolantW > 0) {
            graphics.fill(x + 95, y + 103, x + 95 + coolantW, y + 109, 0xFF45CFE8);
        }

        boolean formed = this.menu.getData().get(4) == 1;
        boolean overheated = this.menu.getData().get(9) == 1;
        boolean running = formed && !overheated && this.menu.getData().get(2) > 0;
        int statusColor = overheated || !formed ? 0xFF3A1A1A : running ? 0xFF1A3A2A : 0xFF273545;
        graphics.fill(x + 166, y + 100, x + 234, y + 114, statusColor);
        graphics.drawCenteredString(this.font,
                Component.translatable(!formed ? "gui.worlds_with_ores.flux.incomplete"
                        : overheated ? "gui.worlds_with_ores.flux.overheated"
                        : running ? "gui.worlds_with_ores.flux.active" : "gui.worlds_with_ores.flux.ready"),
                x + 200, y + 103, overheated || !formed ? COL_BAD : running ? COL_GOOD : COL_TEXT);

        // Inventory panel.
        graphics.fill(x + 30, y + 150, x + this.imageWidth - 30, y + this.imageHeight - 6, COL_PANEL);
        updateButtonLabels();
    }

    private void sampleHistory(int energy, int heat) {
        long now = System.currentTimeMillis() / 500L;
        if (now == this.lastSample) return;
        this.lastSample = now;
        System.arraycopy(energyHistory, 1, energyHistory, 0, energyHistory.length - 1);
        System.arraycopy(heatHistory, 1, heatHistory, 0, heatHistory.length - 1);
        energyHistory[energyHistory.length - 1] = energy;
        heatHistory[heatHistory.length - 1] = heat;
    }

    private void drawGraph(GuiGraphics graphics, int x, int y, int[] values, int color) {
        graphics.fill(x, y, x + 61, y + 17, 0xFF0A0E12);
        for (int i = 0; i < values.length; i++) {
            int height = Math.max(1, values[i] * 15 / 100);
            graphics.fill(x + 2 + i * 2, y + 16 - height, x + 3 + i * 2, y + 16, color);
        }
    }

    private void drawSlotWell(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 2, y - 2, x + 20, y + 20, 0xFF070A0D);
        graphics.fill(x - 1, y - 1, x + 19, y + 19, 0xFF324252);
        graphics.fill(x, y, x + 18, y + 18, 0xFF111820);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 7, COL_TEXT, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.wip"), 118, 7, 0xFFE0B060, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.tier",
                this.menu.getData().get(8) + 1), 210, 7, COL_ACCENT, false);

        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.fuel"), 26, 30, COL_MUTED, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.coolant"), 50, 30, COL_MUTED, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.modules"), 10, 98, COL_MUTED, false);

        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.energy"), 94, 30, COL_MUTED, false);
        graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.heat"), 94, 56, COL_MUTED, false);
        if (this.menu.getData().get(18) > 0) {
            var mismatch = this.menu.getController().getFirstMismatch();
            if (mismatch != null) {
                graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.missing",
                        mismatch.expected().expectedBlock().getName().getString(),
                        mismatch.pos().getX(), mismatch.pos().getY(), mismatch.pos().getZ()),
                        8, 150, COL_BAD, false);
            }
        } else {
            graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.output",
                    this.menu.getData().get(7)), 90, 150, COL_TEXT, false);
            graphics.drawString(this.font, Component.translatable("gui.worlds_with_ores.flux.efficiency",
                    this.menu.getData().get(12)), 170, 150, COL_MUTED, false);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        int x = this.leftPos;
        int y = this.topPos;
        if (mouseX >= x + 94 && mouseX < x + 234 && mouseY >= y + 42 && mouseY < y + 54) {
            graphics.renderTooltip(this.font,
                    Component.translatable("gui.worlds_with_ores.flux.energy_tooltip",
                            this.menu.getData().get(0), this.menu.getData().get(1)),
                    mouseX, mouseY);
        } else if (mouseX >= x + 94 && mouseX < x + 234 && mouseY >= y + 64 && mouseY < y + 74) {
            graphics.renderTooltip(this.font,
                    Component.translatable("gui.worlds_with_ores.flux.heat_tooltip",
                            this.menu.getData().get(5), this.menu.getData().get(6)), mouseX, mouseY);
        } else if (mouseX >= x + 94 && mouseX < x + 160 && mouseY >= y + 102 && mouseY < y + 110) {
            graphics.renderTooltip(this.font,
                    Component.translatable("gui.worlds_with_ores.flux.coolant_tooltip",
                            this.menu.getData().get(10)), mouseX, mouseY);
        }
    }
}
