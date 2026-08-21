package net.millioners.worldswithores.block;

import net.minecraft.util.StringRepresentable;

public enum FluxCoilTier implements StringRepresentable {
    BASIC("basic", 160, 3),
    ADVANCED("advanced", 320, 5),
    QUANTUM("quantum", 640, 8);

    private final String name;
    private final int generation;
    private final int heatPerTick;

    FluxCoilTier(String name, int generation, int heatPerTick) {
        this.name = name;
        this.generation = generation;
        this.heatPerTick = heatPerTick;
    }

    public int generation() {
        return this.generation;
    }

    public int heatPerTick() {
        return this.heatPerTick;
    }

    public FluxCoilTier next() {
        return switch (this) {
            case BASIC -> ADVANCED;
            case ADVANCED -> QUANTUM;
            case QUANTUM -> QUANTUM;
        };
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
