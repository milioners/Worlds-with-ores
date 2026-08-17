package net.millioners.worldswithores.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        return new PortalInfo(Vec3.atCenterOf(spawn), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }

    private BlockPos findOrCreatePortal(BlockPos around) {
        int y = Math.max(this.level.getMinBuildHeight() + 16,
                Math.min(around.getY(), this.level.getMaxBuildHeight() - 16));
        BlockPos center = new BlockPos(around.getX(), y, around.getZ());

        for (int dx = -24; dx <= 24; dx++) {
            for (int dz = -24; dz <= 24; dz++) {
                for (int dy = -12; dy <= 12; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (this.level.getBlockState(check).is(this.portalBlock)) {
                        return check;
                    }
                }
            }
        }

        // Flat platform + complete 2x3 portal facing X (frame along Z width)
        BlockPos base = center;
        Block frame = this.portalBlock.getFrameBlock();
        BlockState frameState = frame.defaultBlockState();

        // Platform under and around portal
        for (int x = -2; x <= 3; x++) {
            for (int z = -2; z <= 2; z++) {
                this.level.setBlock(base.offset(x, -1, z), frameState, 3);
                this.level.setBlock(base.offset(x, 0, z), Blocks.AIR.defaultBlockState(), 3);
                this.level.setBlock(base.offset(x, 1, z), Blocks.AIR.defaultBlockState(), 3);
                this.level.setBlock(base.offset(x, 2, z), Blocks.AIR.defaultBlockState(), 3);
                this.level.setBlock(base.offset(x, 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
        }

        // Frame: interior at (0,0)-(1,2), axis X means portal plane is YZ... 
        // AXIS=X means the portal slab is thin on X? In vanilla, AXIS is the long axis of the portal.
        // For a portal standing in XY plane (facing north/south), AXIS=X.
        Direction.Axis axis = Direction.Axis.X;
        // Bottom and top (y=-1 and y=3), x from -1 to 2
        for (int x = -1; x <= 2; x++) {
            this.level.setBlock(base.offset(x, -1, 0), frameState, 3);
            this.level.setBlock(base.offset(x, 3, 0), frameState, 3);
        }
        // Sides (x=-1 and x=2), y from 0 to 2
        for (int py = 0; py <= 2; py++) {
            this.level.setBlock(base.offset(-1, py, 0), frameState, 3);
            this.level.setBlock(base.offset(2, py, 0), frameState, 3);
        }
        // Corners already included

        // Fill portal interior without neighbor updates first
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

        return base.offset(0, 0, 0);
    }
}
