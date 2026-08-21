package net.millioners.worldswithores.blockentity;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.millioners.worldswithores.registry.ModItems;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/** Typed module bay slots for the Flux Controller (8 fixed roles). */
public enum FluxModuleType {
    OUTPUT(0, () -> ModItems.FLUX_MODULE_OUTPUT),
    EFFICIENCY(1, () -> ModItems.FLUX_MODULE_EFFICIENCY),
    COOLING(2, () -> ModItems.FLUX_MODULE_COOLING),
    CAPACITY(3, () -> ModItems.FLUX_MODULE_CAPACITY),
    HEAT_CAP(4, () -> ModItems.FLUX_MODULE_HEAT_CAP),
    OVERCLOCK(5, () -> ModItems.FLUX_MODULE_OVERCLOCK),
    TRANSFER(6, () -> ModItems.FLUX_MODULE_TRANSFER),
    SAFEGUARD(7, () -> ModItems.FLUX_MODULE_SAFEGUARD);

    public static final int SLOT_COUNT = values().length;

    private final int slot;
    private final Supplier<RegistryObject<Item>> item;

    FluxModuleType(int slot, Supplier<RegistryObject<Item>> item) {
        this.slot = slot;
        this.item = item;
    }

    public int slot() {
        return this.slot;
    }

    public Item item() {
        return this.item.get().get();
    }

    public boolean matches(ItemStack stack) {
        return stack.is(item());
    }

    public static FluxModuleType bySlot(int slot) {
        for (FluxModuleType type : values()) {
            if (type.slot == slot) {
                return type;
            }
        }
        return null;
    }

    public static boolean isModule(ItemStack stack) {
        for (FluxModuleType type : values()) {
            if (type.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    public static int slotFor(ItemStack stack) {
        for (FluxModuleType type : values()) {
            if (type.matches(stack)) {
                return type.slot;
            }
        }
        return -1;
    }
}
