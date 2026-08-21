package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.millioners.worldswithores.block.FluxCoilBlock;
import net.millioners.worldswithores.block.FluxCoilTier;
import net.millioners.worldswithores.block.FluxControllerBlock;
import net.millioners.worldswithores.block.FluxEnergyPortBlock;
import net.millioners.worldswithores.registry.ModBlocks;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

/**
 * Oriented 5x5x5 reactor. The controller and energy port sit at opposite face
 * centers, four coils occupy the remaining face centers, and the 3x3x3 chamber
 * stays empty.
 */
public final class FluxMultiblock {
    public static final int RADIUS = 2;

    public enum Role {
        AIR, CASING, GLASS, COIL, CONTROLLER, PORT;

        public Block expectedBlock() {
            return switch (this) {
                case AIR -> Blocks.AIR;
                case CASING -> ModBlocks.FLUX_CASING.get();
                case GLASS -> ModBlocks.FLUX_GLASS.get();
                case COIL -> ModBlocks.FLUX_COIL.get();
                case CONTROLLER -> ModBlocks.FLUX_CONTROLLER.get();
                case PORT -> ModBlocks.FLUX_ENERGY_PORT.get();
            };
        }
    }

    public record BlueprintCell(BlockPos offset, Role role) {}
    public record Mismatch(BlockPos pos, Role expected, BlockState actual) {}
    public record ValidationResult(List<Mismatch> mismatches) {
        public boolean isComplete() {
            return mismatches.isEmpty();
        }

        public Mismatch firstMismatch() {
            return mismatches.isEmpty() ? null : mismatches.get(0);
        }
    }

    private FluxMultiblock() {}

    public static boolean openController(Level level, BlockPos partPos, Player player) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos check = partPos.offset(dx, dy, dz);
                    if (level.getBlockEntity(check) instanceof FluxControllerBlockEntity controller
                            && isValid(level, check)) {
                        NetworkHooks.openScreen(serverPlayer, controller, check);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isValid(Level level, BlockPos controllerPos) {
        return validateDetailed(level, controllerPos).isComplete();
    }

    public static ValidationResult validateDetailed(Level level, BlockPos controllerPos) {
        BlockState controllerState = level.getBlockState(controllerPos);
        if (!controllerState.is(ModBlocks.FLUX_CONTROLLER.get())
                || !controllerState.hasProperty(FluxControllerBlock.FACING)) {
            return new ValidationResult(List.of(new Mismatch(controllerPos, Role.CONTROLLER, controllerState)));
        }
        Direction outward = controllerState.getValue(FluxControllerBlock.FACING);
        BlockPos center = getCenter(controllerPos, outward);
        List<Mismatch> mismatches = new ArrayList<>();
        for (BlueprintCell cell : blueprint(outward)) {
            BlockPos check = center.offset(cell.offset());
            BlockState actual = level.getBlockState(check);
            boolean matches = cell.role() == Role.AIR
                    ? actual.isAir()
                    : actual.is(cell.role().expectedBlock());
            if (matches && cell.role() == Role.PORT) {
                matches = actual.hasProperty(FluxEnergyPortBlock.FACING)
                        && actual.getValue(FluxEnergyPortBlock.FACING) == outward.getOpposite();
            }
            if (!matches) {
                mismatches.add(new Mismatch(check, cell.role(), actual));
            }
        }
        return new ValidationResult(List.copyOf(mismatches));
    }

    public static List<BlueprintCell> blueprint(Direction outward) {
        List<BlueprintCell> result = new ArrayList<>(125);
        for (int y = -RADIUS; y <= RADIUS; y++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                for (int x = -RADIUS; x <= RADIUS; x++) {
                    BlockPos offset = new BlockPos(x, y, z);
                    result.add(new BlueprintCell(offset, roleAt(offset, outward)));
                }
            }
        }
        return List.copyOf(result);
    }

    public static Role roleAt(BlockPos offset, Direction outward) {
        int x = offset.getX();
        int y = offset.getY();
        int z = offset.getZ();
        int max = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
        if (max < RADIUS) return Role.AIR;
        BlockPos front = BlockPos.ZERO.relative(outward, RADIUS);
        BlockPos back = BlockPos.ZERO.relative(outward.getOpposite(), RADIUS);
        Direction side = outward.getClockWise();
        if (offset.equals(front)) return Role.CONTROLLER;
        if (offset.equals(back)) return Role.PORT;
        if (offset.equals(BlockPos.ZERO.above(RADIUS)) || offset.equals(BlockPos.ZERO.below(RADIUS))
                || offset.equals(BlockPos.ZERO.relative(side, RADIUS))
                || offset.equals(BlockPos.ZERO.relative(side.getOpposite(), RADIUS))) {
            return Role.COIL;
        }
        return isFaceInterior(x, y, z) ? Role.GLASS : Role.CASING;
    }

    private static boolean isFaceInterior(int x, int y, int z) {
        int faces = (Math.abs(x) == RADIUS ? 1 : 0)
                + (Math.abs(y) == RADIUS ? 1 : 0)
                + (Math.abs(z) == RADIUS ? 1 : 0);
        return faces == 1;
    }

    public static BlockPos getCenter(BlockPos controllerPos, Direction outward) {
        return controllerPos.relative(outward.getOpposite(), RADIUS);
    }

    public static FluxCoilTier getTier(Level level, BlockPos controllerPos) {
        BlockState controllerState = level.getBlockState(controllerPos);
        if (!controllerState.hasProperty(FluxControllerBlock.FACING)) {
            return FluxCoilTier.BASIC;
        }
        Direction outward = controllerState.getValue(FluxControllerBlock.FACING);
        Direction side = outward.getClockWise();
        BlockPos center = getCenter(controllerPos, outward);
        BlockPos[] coils = {
                center.above(RADIUS), center.below(RADIUS),
                center.relative(side, RADIUS), center.relative(side.getOpposite(), RADIUS)
        };
        FluxCoilTier result = FluxCoilTier.QUANTUM;
        for (BlockPos coil : coils) {
            BlockState state = level.getBlockState(coil);
            if (!state.is(ModBlocks.FLUX_COIL.get()) || !state.hasProperty(FluxCoilBlock.TIER)) {
                return FluxCoilTier.BASIC;
            }
            FluxCoilTier tier = state.getValue(FluxCoilBlock.TIER);
            if (tier.ordinal() < result.ordinal()) {
                result = tier;
            }
        }
        return result;
    }

    public static BlockPos getEnergyPort(Level level, BlockPos controllerPos) {
        BlockState state = level.getBlockState(controllerPos);
        if (!state.hasProperty(FluxControllerBlock.FACING)) {
            return controllerPos;
        }
        Direction outward = state.getValue(FluxControllerBlock.FACING);
        return getCenter(controllerPos, outward).relative(outward.getOpposite(), RADIUS);
    }

    public static BlockPos findController(Level level, BlockPos near) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos check = near.offset(dx, dy, dz);
                    if (level.getBlockEntity(check) instanceof FluxControllerBlockEntity
                            && isValid(level, check)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    public static void notifyNeighbors(Level level, BlockPos controllerPos) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos pos = controllerPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                }
            }
        }
    }
}
