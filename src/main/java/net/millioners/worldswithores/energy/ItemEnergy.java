package net.millioners.worldswithores.energy;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemEnergy {
    private ItemEnergy() {}

    public static int getEnergy(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Energy");
    }

    public static void setEnergy(ItemStack stack, int energy) {
        stack.getOrCreateTag().putInt("Energy", Math.max(0, energy));
    }

    public static int receive(ItemStack stack, int capacity, int maxReceive, int amount, boolean simulate) {
        int energy = getEnergy(stack);
        int received = Math.min(capacity - energy, Math.min(maxReceive, amount));
        if (!simulate && received > 0) {
            setEnergy(stack, energy + received);
        }
        return Math.max(0, received);
    }

    public static int extract(ItemStack stack, int maxExtract, int amount, boolean simulate) {
        int energy = getEnergy(stack);
        int extracted = Math.min(energy, Math.min(maxExtract, amount));
        if (!simulate && extracted > 0) {
            setEnergy(stack, energy - extracted);
        }
        return Math.max(0, extracted);
    }

    public static ICapabilityProvider provider(ItemStack stack, int capacity, int maxTransfer) {
        return new ICapabilityProvider() {
            private final IEnergyStorage storage = new IEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return receive(stack, capacity, maxTransfer, maxReceive, simulate);
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return 0;
                }

                @Override
                public int getEnergyStored() {
                    return getEnergy(stack);
                }

                @Override
                public int getMaxEnergyStored() {
                    return capacity;
                }

                @Override
                public boolean canExtract() {
                    return false;
                }

                @Override
                public boolean canReceive() {
                    return true;
                }
            };
            private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> storage);

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return ForgeCapabilities.ENERGY.orEmpty(cap, optional);
            }
        };
    }

    public static void ensureTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Energy")) {
            tag.putInt("Energy", 0);
        }
    }
}
