package net.millioners.worldswithores.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Resolves optional mod frame blocks; falls back to our own blocks when the mod is absent.
 */
public final class SoftFrames {
    private SoftFrames() {}

    public static Block resolve(String preferredId, Supplier<Block> fallback) {
        Block preferred = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(preferredId));
        if (preferred != null && preferred != Blocks.AIR) {
            return preferred;
        }
        return fallback.get();
    }

    public static Supplier<Block> soft(String preferredId, Supplier<Block> fallback) {
        return () -> resolve(preferredId, fallback);
    }

    /** Preferred (if loaded) + always-available fallback, for portal lighting/validation. */
    @SafeVarargs
    public static List<Supplier<Block>> pair(String preferredId, Supplier<Block> fallback, Supplier<Block>... extra) {
        List<Supplier<Block>> list = new ArrayList<>();
        list.add(() -> {
            Block preferred = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(preferredId));
            return preferred == null ? Blocks.AIR : preferred;
        });
        list.add(fallback);
        for (Supplier<Block> e : extra) {
            list.add(e);
        }
        return list;
    }

    public static List<Block> resolveAll(List<Supplier<Block>> suppliers) {
        List<Block> out = new ArrayList<>();
        for (Supplier<Block> supplier : suppliers) {
            Block block = supplier.get();
            if (block != null && block != Blocks.AIR && !out.contains(block)) {
                out.add(block);
            }
        }
        return out;
    }
}
