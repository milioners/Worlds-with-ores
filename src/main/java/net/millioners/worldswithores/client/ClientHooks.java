package net.millioners.worldswithores.client;

import net.minecraft.client.Minecraft;

public final class ClientHooks {
    private ClientHooks() {}

    public static void openRecipeBook() {
        Minecraft.getInstance().setScreen(new RecipeBookScreen());
    }
}
