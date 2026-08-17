package net.millioners.worldswithores.client;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.millioners.worldswithores.WorldsWithOresMod;

/**
 * Skips vanilla "Experimental settings" confirm/backup screens caused by custom dimensions.
 */
@Mod.EventBusSubscriber(modid = WorldsWithOresMod.MOD_ID, value = Dist.CLIENT)
public final class SuppressExperimentalWarning {
    private SuppressExperimentalWarning() {}

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        Screen screen = event.getNewScreen();
        if (screen == null) {
            return;
        }
        if (!isExperimentalWarning(screen)) {
            return;
        }
        if (autoConfirm(screen)) {
            event.setCanceled(true);
        }
    }

    private static boolean isExperimentalWarning(Screen screen) {
        Component title = screen.getTitle();
        if (containsExperimentalKey(title)) {
            return true;
        }
        // BackupConfirmScreen uses description components too
        if (screen instanceof BackupConfirmScreen || screen instanceof ConfirmScreen) {
            String joined = title.getString().toLowerCase();
            return joined.contains("experimental")
                    || joined.contains("экспериментальн")
                    || joined.contains("экспериментальн");
        }
        return false;
    }

    private static boolean containsExperimentalKey(Component component) {
        if (component == null) {
            return false;
        }
        if (component.getContents() instanceof TranslatableContents tc) {
            String key = tc.getKey();
            if (key != null && (key.contains("experimental") || key.contains("selectWorld.warning.experimental")
                    || key.contains("selectWorld.backupQuestion.experimental")
                    || key.contains("selectWorld.backupWarning.experimental"))) {
                return true;
            }
        }
        for (Component sibling : component.getSiblings()) {
            if (containsExperimentalKey(sibling)) {
                return true;
            }
        }
        return false;
    }

    private static boolean autoConfirm(Screen screen) {
        // Prefer clicking the confirm/"Proceed" button so CreateWorldScreen continues correctly.
        for (GuiEventListener listener : screen.children()) {
            if (listener instanceof Button button && isProceedButton(button)) {
                button.onPress();
                return true;
            }
        }
        // Fallback: first button is usually Proceed / Yes
        for (GuiEventListener listener : screen.children()) {
            if (listener instanceof Button button) {
                button.onPress();
                return true;
            }
        }
        return false;
    }

    private static boolean isProceedButton(AbstractWidget widget) {
        String label = widget.getMessage().getString().toLowerCase();
        if (widget.getMessage().getContents() instanceof TranslatableContents tc) {
            String key = tc.getKey();
            if (key != null && (key.equals("gui.proceed") || key.equals("selectWorld.recreate")
                    || key.equals("menu.confirmScreen.continue") || key.contains("proceed"))) {
                return true;
            }
        }
        return label.contains("proceed") || label.contains("продолж") || label.contains("да")
                || label.contains("create") || label.contains("создать") || label.contains("i know");
    }
}
