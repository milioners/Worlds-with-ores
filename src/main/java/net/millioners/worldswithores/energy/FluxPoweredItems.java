package net.millioners.worldswithores.energy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Shared FE helpers for Flux-powered tools and armor. */
public final class FluxPoweredItems {
    private FluxPoweredItems() {}

    public static boolean hasEnergy(ItemStack stack, int cost) {
        return ItemEnergy.getEnergy(stack) >= cost;
    }

    public static boolean tryConsume(ItemStack stack, int cost) {
        if (!hasEnergy(stack, cost)) {
            return false;
        }
        ItemEnergy.extract(stack, cost, cost, false);
        return true;
    }

    public static void appendEnergyTooltip(ItemStack stack, int capacity, List<Component> tip) {
        tip.add(Component.translatable("tooltip.worlds_with_ores.flux_energy",
                ItemEnergy.getEnergy(stack), capacity));
    }

    public static int barWidth(ItemStack stack, int capacity) {
        return Math.round(13.0F * ItemEnergy.getEnergy(stack) / (float) Math.max(1, capacity));
    }

    public static int barColor() {
        return 0x6FA8C8;
    }
}
