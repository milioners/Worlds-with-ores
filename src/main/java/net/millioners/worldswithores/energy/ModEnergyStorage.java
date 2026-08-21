package net.millioners.worldswithores.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;

public class ModEnergyStorage extends EnergyStorage {
    private final Runnable onChanged;

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable onChanged) {
        super(capacity, maxReceive, maxExtract);
        this.onChanged = onChanged;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) {
            this.onChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate) {
            this.onChanged.run();
        }
        return extracted;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, this.capacity));
        this.onChanged.run();
    }

    public void addEnergy(int amount) {
        setEnergy(this.energy + amount);
    }

    public void setCapacity(int capacity) {
        int newCapacity = Math.max(1, capacity);
        if (this.capacity == newCapacity) {
            return;
        }
        this.capacity = newCapacity;
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        }
        this.onChanged.run();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", this.energy);
        return tag;
    }

    public void load(CompoundTag tag) {
        this.energy = tag.getInt("Energy");
    }
}
