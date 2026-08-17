package net.millioners.worldswithores.world.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

/**
 * Places an ore vein only if the target block exists (soft dependency on other mods)
 * and optionally only in a specific dimension.
 */
public class OptionalOreFeature extends Feature<OptionalOreFeature.Config> {
    public OptionalOreFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();

        if (config.dimension.isPresent()) {
            ResourceLocation dim = config.dimension.get();
            if (!level.getLevel().dimension().location().equals(dim)) {
                return false;
            }
        }

        Block oreBlock = ForgeRegistries.BLOCKS.getValue(config.ore);
        if (oreBlock == null || oreBlock.defaultBlockState().isAir()) {
            return false;
        }

        BlockState oreState = oreBlock.defaultBlockState();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        boolean placed = false;

        int size = Math.max(1, config.size);
        for (int i = 0; i < size; i++) {
            BlockPos pos = origin.offset(
                    random.nextInt(4) - random.nextInt(4),
                    random.nextInt(4) - random.nextInt(4),
                    random.nextInt(4) - random.nextInt(4)
            );
            BlockState current = level.getBlockState(pos);
            if (isReplaceable(current, config.replaceables)) {
                level.setBlock(pos, oreState, 2);
                placed = true;
            }
        }
        return placed;
    }

    private static boolean isReplaceable(BlockState state, List<ResourceLocation> replaceables) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (key == null) {
            return false;
        }
        if (replaceables != null) {
            for (ResourceLocation rl : replaceables) {
                if (rl.equals(key)) {
                    return true;
                }
            }
        }
        String path = key.getPath();
        return path.endsWith("_ore") || path.equals("stone") || path.equals("deepslate")
                || path.equals("netherrack") || path.equals("end_stone") || path.equals("tuff")
                || path.equals("andesite") || path.equals("diorite") || path.equals("granite");
    }

    public static class Config implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("ore").forGetter(c -> c.ore),
                Codec.INT.optionalFieldOf("size", 8).forGetter(c -> c.size),
                ResourceLocation.CODEC.listOf().optionalFieldOf("replaceables", List.of(
                        new ResourceLocation("minecraft:stone"),
                        new ResourceLocation("minecraft:deepslate"),
                        new ResourceLocation("minecraft:coal_ore"),
                        new ResourceLocation("minecraft:iron_ore"),
                        new ResourceLocation("minecraft:gold_ore"),
                        new ResourceLocation("minecraft:diamond_ore"),
                        new ResourceLocation("minecraft:emerald_ore"),
                        new ResourceLocation("minecraft:lapis_ore"),
                        new ResourceLocation("minecraft:redstone_ore")
                )).forGetter(c -> c.replaceables),
                ResourceLocation.CODEC.optionalFieldOf("dimension").forGetter(c -> c.dimension)
        ).apply(instance, Config::new));

        public final ResourceLocation ore;
        public final int size;
        public final List<ResourceLocation> replaceables;
        public final Optional<ResourceLocation> dimension;

        public Config(ResourceLocation ore, int size, List<ResourceLocation> replaceables, Optional<ResourceLocation> dimension) {
            this.ore = ore;
            this.size = size;
            this.replaceables = replaceables;
            this.dimension = dimension;
        }
    }
}
