package net.millioners.worldswithores.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.millioners.worldswithores.block.ModPortalBlock;

import java.util.function.Function;

public class ModTeleporter implements ITeleporter {
    private final ServerLevel level;
    private final ModPortalBlock portalBlock;

    public ModTeleporter(ServerLevel level, ModPortalBlock portalBlock) {
        this.level = level;
        this.portalBlock = portalBlock;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        BlockPos spawn = findOrCreatePortal(BlockPos.containing(entity.position()));
        return new PortalInfo(Vec3.atBottomCenterOf(spawn).add(0.0D, 0.05D, 0.0D), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }

    private BlockPos findOrCreatePortal(BlockPos around) {
        BlockPos existing = findExistingPortal(around);
        if (existing != null) {
            return existing;
        }

        BlockPos ground = findSolidGround(around.getX(), around.getZ());
        return createPortalAt(ground);
    }

    private BlockPos findExistingPortal(BlockPos around) {
        int centerY = clampY(around.getY());
        BlockPos center = new BlockPos(around.getX(), centerY, around.getZ());
        for (int dx = -32; dx <= 32; dx++) {
            for (int dz = -32; dz <= 32; dz++) {
                for (int dy = -24; dy <= 24; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (!this.level.isInWorldBounds(check)) {
                        continue;
                    }
                    if (this.level.getBlockState(check).is(this.portalBlock)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a solid surface and returns the block position ON TOP of that surface
     * (where the portal interior bottom / player feet should be).
     */
    private BlockPos findSolidGround(int x, int z) {
        int bestScore = Integer.MIN_VALUE;
        BlockPos best = null;

        for (int radius = 0; radius <= 16; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    int px = x + dx;
                    int pz = z + dz;
                    int surface = locateSurfaceY(px, pz);
                    if (surface == Integer.MIN_VALUE) {
                        continue;
                    }
                    BlockPos candidate = new BlockPos(px, surface, pz);
                    int score = scoreGround(candidate);
                    if (score > bestScore) {
                        bestScore = score;
                        best = candidate;
                    }
                }
            }
            if (best != null && bestScore >= 50) {
                break;
            }
        }

        if (best != null) {
            return best;
        }

        // Absolute fallback: force a mid-height platform
        int fallbackY = Math.max(this.level.getMinBuildHeight() + 32, Math.min(64, this.level.getMaxBuildHeight() - 16));
        return new BlockPos(x, fallbackY, z);
    }

    private int locateSurfaceY(int x, int z) {
        int min = this.level.getMinBuildHeight() + 2;
        int max = this.level.getMaxBuildHeight() - 8;

        int heightmap = this.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (heightmap > min && heightmap < max) {
            BlockPos below = new BlockPos(x, heightmap - 1, z);
            if (isStableGround(below) && hasClearance(new BlockPos(x, heightmap, z))) {
                return heightmap;
            }
        }

        for (int y = Math.min(max, 192); y >= min; y--) {
            BlockPos ground = new BlockPos(x, y, z);
            BlockPos stand = ground.above();
            if (isStableGround(ground) && hasClearance(stand) && !isDangerous(stand)) {
                return stand.getY();
            }
        }
        return Integer.MIN_VALUE;
    }

    private int scoreGround(BlockPos standPos) {
        BlockPos ground = standPos.below();
        if (!isStableGround(ground) || !hasClearance(standPos) || isDangerous(standPos)) {
            return Integer.MIN_VALUE;
        }
        int score = 100;
        BlockState groundState = this.level.getBlockState(ground);
        if (groundState.is(BlockTags.BASE_STONE_OVERWORLD) || groundState.is(Blocks.STONE) || groundState.is(Blocks.DEEPSLATE)
                || groundState.is(Blocks.NETHERRACK) || groundState.is(Blocks.END_STONE)
                || groundState.getBlock().getDescriptionId().contains("ore")) {
            score += 20;
        }
        if (groundState.is(Blocks.SAND) || groundState.is(Blocks.GRAVEL)) {
            score -= 15;
        }
        // Prefer flatter surroundings
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = ground.relative(dir);
            if (isStableGround(side)) {
                score += 5;
            }
        }
        return score;
    }

    private boolean isStableGround(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        if (state.isAir() || state.getCollisionShape(this.level, pos).isEmpty()) {
            return false;
        }
        if (state.getFluidState().is(Fluids.LAVA) || state.getFluidState().is(Fluids.WATER)) {
            return false;
        }
        if (state.is(Blocks.LAVA) || state.is(Blocks.WATER) || state.is(Blocks.MAGMA_BLOCK)
                || state.is(Blocks.CACTUS) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
            return false;
        }
        return !state.getCollisionShape(this.level, pos).isEmpty();
    }

    private boolean hasClearance(BlockPos standPos) {
        for (int dy = 0; dy <= 4; dy++) {
            BlockPos pos = standPos.above(dy);
            BlockState state = this.level.getBlockState(pos);
            if (!state.isAir() && !state.canBeReplaced() && !state.getCollisionShape(this.level, pos).isEmpty()) {
                // Allow replacing non-solid foliage-ish later; for scoring require mostly clear
                if (dy <= 3 && !state.getCollisionShape(this.level, pos).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isDangerous(BlockPos standPos) {
        for (int dy = -1; dy <= 2; dy++) {
            BlockState state = this.level.getBlockState(standPos.above(dy));
            if (state.is(Blocks.LAVA) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)
                    || state.getFluidState().is(Fluids.LAVA)) {
                return true;
            }
        }
        return false;
    }

    private int clampY(int y) {
        return Math.max(this.level.getMinBuildHeight() + 16, Math.min(y, this.level.getMaxBuildHeight() - 16));
    }

    private BlockPos createPortalAt(BlockPos standPos) {
        // standPos = feet / portal interior bottom
        BlockPos base = standPos;
        Block frame = this.portalBlock.getFrameBlock();
        BlockState frameState = frame.defaultBlockState();
        BlockState platform = Blocks.STONE.defaultBlockState();

        // Prefer stone platform so portals are not floating on replaced air
        for (int x = -2; x <= 3; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos floor = base.offset(x, -1, z);
                BlockPos under = floor.below();
                if (!isStableGround(under)) {
                    this.level.setBlock(under, platform, 3);
                }
                this.level.setBlock(floor, platform, 3);
                for (int clearY = 0; clearY <= 3; clearY++) {
                    this.level.setBlock(base.offset(x, clearY, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        Direction.Axis axis = Direction.Axis.X;
        for (int x = -1; x <= 2; x++) {
            this.level.setBlock(base.offset(x, -1, 0), frameState, 3);
            this.level.setBlock(base.offset(x, 3, 0), frameState, 3);
        }
        for (int py = 0; py <= 2; py++) {
            this.level.setBlock(base.offset(-1, py, 0), frameState, 3);
            this.level.setBlock(base.offset(2, py, 0), frameState, 3);
        }

        BlockState portalState = this.portalBlock.defaultBlockState().setValue(ModPortalBlock.AXIS, axis);
        for (int x = 0; x <= 1; x++) {
            for (int py = 0; py <= 2; py++) {
                this.level.setBlock(base.offset(x, py, 0), portalState, 2 | 16);
            }
        }
        for (int x = 0; x <= 1; x++) {
            for (int py = 0; py <= 2; py++) {
                this.level.blockUpdated(base.offset(x, py, 0), this.portalBlock);
            }
        }

        return base;
    }
}
