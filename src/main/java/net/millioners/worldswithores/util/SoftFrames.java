package net.millioners.worldswithores.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Resolves optional mod frame blocks for industrial portals.
 */
public final class SoftFrames {
    private SoftFrames() {}

    public static Block resolve(String preferredId) {
        Block preferred = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(preferredId));
        if (preferred != null && preferred != Blocks.AIR) {
            return preferred;
        }
        return Blocks.AIR;
    }

    public static Block resolve(String preferredId, Supplier<Block> fallback) {
        Block preferred = resolve(preferredId);
        return preferred != Blocks.AIR ? preferred : fallback.get();
    }

    public static Supplier<Block> soft(String preferredId) {
        return () -> resolve(preferredId);
    }

    public static List<Supplier<Block>> only(String preferredId) {
        return List.of(soft(preferredId));
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
