package net.millioners.worldswithores.energy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.millioners.worldswithores.registry.ModItems;

public final class PortalFuels {
    public record FuelStats(int energy, int heatPercent) {
        public static final FuelStats EMPTY = new FuelStats(0, 100);
    }

    private PortalFuels() {}

    public static FuelStats stats(ItemStack stack) {
        if (stack.isEmpty()) {
            return FuelStats.EMPTY;
        }
        Item item = stack.getItem();
        if (item == ModItems.INGOT_COAL.get() || item == ModItems.INGOT_IRON.get()
                || item == ModItems.INGOT_GOLD.get() || item == ModItems.INGOT_LAPIS.get()
                || item == ModItems.INGOT_REDSTONE.get()) {
            return new FuelStats(32_000, 85);
        }
        if (item == ModItems.INGOT_DIAMOND.get() || item == ModItems.INGOT_EMERALD.get()) {
            return new FuelStats(64_000, 75);
        }
        if (item == ModItems.CATALYST_COAL.get() || item == ModItems.CATALYST_IRON.get()
                || item == ModItems.CATALYST_GOLD.get() || item == ModItems.CATALYST_LAPIS.get()
                || item == ModItems.CATALYST_REDSTONE.get()) {
            return new FuelStats(12_000, 125);
        }
        if (item == ModItems.CATALYST_DIAMOND.get() || item == ModItems.CATALYST_EMERALD.get()) {
            return new FuelStats(24_000, 110);
        }
        return FuelStats.EMPTY;
    }

    public static int burnEnergy(ItemStack stack) {
        return stats(stack).energy();
    }

    public static boolean isFuel(ItemStack stack) {
        return burnEnergy(stack) > 0;
    }
}
