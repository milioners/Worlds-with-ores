package net.millioners.worldswithores.energy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.millioners.worldswithores.registry.ModItems;

public final class PortalFuels {
    private PortalFuels() {}

    public static int burnEnergy(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Item item = stack.getItem();
        if (item == ModItems.INGOT_COAL.get() || item == ModItems.INGOT_IRON.get()
                || item == ModItems.INGOT_GOLD.get() || item == ModItems.INGOT_LAPIS.get()
                || item == ModItems.INGOT_REDSTONE.get()) {
            return 8_000;
        }
        if (item == ModItems.INGOT_DIAMOND.get() || item == ModItems.INGOT_EMERALD.get()) {
            return 16_000;
        }
        if (item == ModItems.CATALYST_COAL.get() || item == ModItems.CATALYST_IRON.get()
                || item == ModItems.CATALYST_GOLD.get() || item == ModItems.CATALYST_LAPIS.get()
                || item == ModItems.CATALYST_REDSTONE.get()) {
            return 4_000;
        }
        if (item == ModItems.CATALYST_DIAMOND.get() || item == ModItems.CATALYST_EMERALD.get()) {
            return 8_000;
        }
        return 0;
    }

    public static boolean isFuel(ItemStack stack) {
        return burnEnergy(stack) > 0;
    }
}
