package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.millioners.worldswithores.registry.ModBlocks;

/**
 * 3x3x3 cube: controller in center, coils on 6 faces, casing on edges/corners.
 */
public final class FluxMultiblock {
    private FluxMultiblock() {}

    public static boolean isValid(Level level, BlockPos controllerPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    BlockPos check = controllerPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(check);
                    int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (manhattan == 1) {
                        if (!state.is(ModBlocks.FLUX_COIL.get())) {
                            return false;
                        }
                    } else {
                        if (!state.is(ModBlocks.FLUX_CASING.get())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void notifyNeighbors(Level level, BlockPos controllerPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = controllerPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                }
            }
        }
    }
}
