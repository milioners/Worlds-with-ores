package net.millioners.worldswithores.world.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.millioners.worldswithores.util.SoftFrames;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Soft-fills chunk columns with weighted mod ores when those blocks exist.
 * Supports legacy single {@code ore} configs and new {@code ores:[{id,weight}]} lists.
 */
public class SoftTerrainFillFeature extends Feature<SoftTerrainFillFeature.Config> {
    public SoftTerrainFillFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        List<WeightedOre> resolved = resolveOres(config);
        if (resolved.isEmpty()) {
            Block fallback = SoftFrames.resolve("minecraft:calcite", () -> Blocks.CALCITE);
            if (fallback == null || fallback == Blocks.AIR) {
                return false;
            }
            resolved = List.of(new WeightedOre(ForgeRegistries.BLOCKS.getKey(fallback), 1));
        }

        int totalWeight = 0;
        List<BlockState> states = new ArrayList<>(resolved.size());
        List<Integer> weights = new ArrayList<>(resolved.size());
        for (WeightedOre entry : resolved) {
            Block block = SoftFrames.resolve(entry.id().toString(), () -> Blocks.AIR);
            if (block == null || block == Blocks.AIR) {
                continue;
            }
            states.add(block.defaultBlockState());
            weights.add(Math.max(1, entry.weight()));
            totalWeight += Math.max(1, entry.weight());
        }
        if (states.isEmpty() || totalWeight <= 0) {
            return false;
        }

        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        int minY = Math.max(level.getMinBuildHeight(), 0);
        int maxY = Math.min(level.getMaxBuildHeight(), 128);
        boolean placed = false;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = chunkPos.getMinBlockX() + dx;
                int worldZ = chunkPos.getMinBlockZ() + dz;
                for (int y = minY; y < maxY; y++) {
                    BlockPos pos = new BlockPos(worldX, y, worldZ);
                    BlockState current = level.getBlockState(pos);
                    if (!isReplaceable(current, config.replaceables())) {
                        continue;
                    }
                    BlockState chosen = pick(states, weights, totalWeight, random, y);
                    level.setBlock(pos, chosen, 2);
                    placed = true;
                }
            }
        }
        return placed;
    }

    private static List<WeightedOre> resolveOres(Config config) {
        if (config.ores() != null && !config.ores().isEmpty()) {
            return config.ores();
        }
        return config.ore().map(id -> List.of(new WeightedOre(id, 1))).orElse(List.of());
    }

    private static BlockState pick(List<BlockState> states, List<Integer> weights, int totalWeight,
                                   RandomSource random, int y) {
        // Slightly favor the first (primary) ore near the surface and deepslate-ish ores lower.
        int roll = random.nextInt(totalWeight);
        int cursor = 0;
        for (int i = 0; i < states.size(); i++) {
            cursor += weights.get(i);
            if (roll < cursor) {
                    BlockState state = states.get(i);
                    ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                    String path = key == null ? "" : key.getPath();
                    if (path.contains("deepslate") && y > 48 && random.nextFloat() < 0.65F && !states.isEmpty()) {
                        return states.get(0);
                    }
                    if (!path.contains("deepslate") && y < 16 && states.size() > 1 && random.nextFloat() < 0.35F) {
                        for (BlockState candidate : states) {
                            ResourceLocation candidateKey = ForgeRegistries.BLOCKS.getKey(candidate.getBlock());
                            String candidatePath = candidateKey == null ? "" : candidateKey.getPath();
                            if (candidatePath.contains("deepslate")) {
                                return candidate;
                            }
                        }
                    }
                    return state;
            }
        }
        return states.get(0);
    }

    private static boolean isReplaceable(BlockState state, List<ResourceLocation> replaceables) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (key == null) {
            return false;
        }
        for (ResourceLocation replaceable : replaceables) {
            if (replaceable.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public record WeightedOre(ResourceLocation id, int weight) {
        public static final Codec<WeightedOre> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(WeightedOre::id),
                Codec.INT.optionalFieldOf("weight", 1).forGetter(WeightedOre::weight)
        ).apply(instance, WeightedOre::new));
    }

    public record Config(Optional<ResourceLocation> ore, List<WeightedOre> ores, List<ResourceLocation> replaceables)
            implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("ore").forGetter(Config::ore),
                WeightedOre.CODEC.listOf().optionalFieldOf("ores", List.of()).forGetter(Config::ores),
                ResourceLocation.CODEC.listOf().fieldOf("replaceables").forGetter(Config::replaceables)
        ).apply(instance, Config::new));
    }
}
