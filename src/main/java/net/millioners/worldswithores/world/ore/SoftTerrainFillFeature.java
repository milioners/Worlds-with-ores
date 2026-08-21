package net.millioners.worldswithores.world.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

import java.util.List;

/**
 * Soft-fills a whole chunk column with a mod ore block when that block exists.
 * Base terrain stays as a vanilla placeholder so datapacks always load.
 */
public class SoftTerrainFillFeature extends Feature<SoftTerrainFillFeature.Config> {
    public SoftTerrainFillFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        // Prefer the industrial ore; if the soft dependency is missing, keep solid terrain
        // instead of leaving sparse nether-noise stone for decorative veins to shred.
        Block oreBlock = SoftFrames.resolve(config.ore.toString(), () -> Blocks.CALCITE);
        if (oreBlock == null || oreBlock == Blocks.AIR) {
            return false;
        }

        WorldGenLevel level = context.level();
        BlockState oreState = oreBlock.defaultBlockState();
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
                    if (isReplaceable(current, config.replaceables)) {
                        level.setBlock(pos, oreState, 2);
                        placed = true;
                    }
                }
            }
        }
        return placed;
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

    public record Config(ResourceLocation ore, List<ResourceLocation> replaceables) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("ore").forGetter(Config::ore),
                ResourceLocation.CODEC.listOf().fieldOf("replaceables").forGetter(Config::replaceables)
        ).apply(instance, Config::new));
    }
}
