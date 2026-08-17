package net.millioners.worldswithores.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.menu.ModChestMenu;

public class ModChestScreen extends AbstractContainerScreen<ModChestMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(WorldsWithOresMod.MOD_ID, "textures/chest_gui.png");

    public ModChestScreen(ModChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // Matches original MCreator GUI + chest_gui.png (250x166)
        this.imageWidth = 250;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = 8;
        this.titleLabelY = -10; // hide overlapping title; texture has its own look
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Texture already includes labels; skip default text overlay
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 250, 166);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
