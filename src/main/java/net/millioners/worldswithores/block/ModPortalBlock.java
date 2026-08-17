package net.millioners.worldswithores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.millioners.worldswithores.world.ModTeleporter;

import java.util.function.Supplier;

public class ModPortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    private final Supplier<Block> frameBlock;
    private final ResourceKey<Level> destination;

    public ModPortalBlock(Supplier<Block> frameBlock, ResourceKey<Level> destination) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .noCollission()
                .randomTicks()
                .strength(-1.0F)
                .sound(SoundType.GLASS)
                .lightLevel(state -> 11)
                .noLootTable()
                .pushReaction(PushReaction.BLOCK));
        this.frameBlock = frameBlock;
        this.destination = destination;
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    public Block getFrameBlock() {
        return this.frameBlock.get();
    }

    public ResourceKey<Level> getDestination() {
        return this.destination;
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis axis = state.getValue(AXIS);
        Direction.Axis dirAxis = direction.getAxis();
        boolean alongPortal = dirAxis == axis || dirAxis == Direction.Axis.Y;
        if (!alongPortal || neighborState.is(this) || isCompletePortal(level, pos, axis)) {
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    private boolean isCompletePortal(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        // Find bottom-left of this portal column/row group by scanning
        Direction right = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        Direction left = right.getOpposite();

        BlockPos.MutableBlockPos bottom = pos.mutable();
        while (level.getBlockState(bottom.below()).is(this)) {
            bottom.move(Direction.DOWN);
        }
        while (level.getBlockState(bottom.relative(left)).is(this)) {
            bottom.move(left);
        }

        int width = 0;
        BlockPos.MutableBlockPos cursor = bottom.mutable();
        while (width < 21 && level.getBlockState(cursor).is(this)) {
            width++;
            cursor.move(right);
        }
        int height = 0;
        cursor.set(bottom);
        while (height < 21 && level.getBlockState(cursor).is(this)) {
            height++;
            cursor.move(Direction.UP);
        }
        if (width < 2 || height < 3) {
            return false;
        }

        Block frame = this.getFrameBlock();
        for (int x = 0; x < width; x++) {
            if (!level.getBlockState(bottom.relative(right, x).below()).is(frame)) {
                return false;
            }
            if (!level.getBlockState(bottom.relative(right, x).above(height)).is(frame)) {
                return false;
            }
        }
        for (int y = 0; y < height; y++) {
            if (!level.getBlockState(bottom.relative(left).above(y)).is(frame)) {
                return false;
            }
            if (!level.getBlockState(bottom.relative(right, width).above(y)).is(frame)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(level instanceof ServerLevel serverLevel) || !entity.canChangeDimensions()
                || entity.isPassenger() || entity.isVehicle() || !entity.isAlive()) {
            return;
        }
        if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
            return;
        }
        entity.setPortalCooldown();
        ResourceKey<Level> targetKey = serverLevel.dimension().equals(this.destination) ? Level.OVERWORLD : this.destination;
        ServerLevel target = serverLevel.getServer().getLevel(targetKey);
        if (target == null) {
            return;
        }
        entity.changeDimension(target, new ModTeleporter(target, this));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double vx = (random.nextDouble() - 0.5D) * 0.5D;
            double vy = (random.nextDouble() - 0.5D) * 0.5D;
            double vz = (random.nextDouble() - 0.5D) * 0.5D;
            int j = random.nextInt(2) * 2 - 1;
            if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
                x = pos.getX() + 0.5D + 0.25D * j;
                vx = random.nextFloat() * 2.0F * j;
            } else {
                z = pos.getZ() + 0.5D + 0.25D * j;
                vz = random.nextFloat() * 2.0F * j;
            }
            level.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    public static boolean trySpawnPortal(Level level, BlockPos pos, Block frame, ModPortalBlock portal) {
        if (level.isClientSide) {
            return false;
        }
        BlockPos[] seeds = new BlockPos[]{
                pos, pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()
        };
        for (BlockPos seed : seeds) {
            for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
                PortalFrame found = findFrame(level, seed, axis, frame);
                if (found != null) {
                    fillPortal(level, found, portal);
                    return true;
                }
            }
        }
        return false;
    }

    private static PortalFrame findFrame(Level level, BlockPos seed, Direction.Axis axis, Block frame) {
        if (!isEmpty(level, seed)) {
            return null;
        }

        Direction right = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        Direction left = right.getOpposite();

        BlockPos.MutableBlockPos bottom = seed.mutable();
        while (isEmpty(level, bottom.below()) && bottom.getY() > level.getMinBuildHeight() + 1) {
            bottom.move(Direction.DOWN);
        }
        if (!level.getBlockState(bottom.below()).is(frame)) {
            return null;
        }

        while (isEmpty(level, bottom.relative(left))) {
            bottom.move(left);
            if (!level.getBlockState(bottom.below()).is(frame)) {
                return null;
            }
        }
        if (!level.getBlockState(bottom.relative(left)).is(frame)) {
            return null;
        }

        int width = 0;
        BlockPos.MutableBlockPos cursor = bottom.mutable();
        while (width < 21 && isEmpty(level, cursor) && level.getBlockState(cursor.below()).is(frame)) {
            width++;
            cursor.move(right);
        }
        if (width < 2 || width > 21 || !level.getBlockState(bottom.relative(right, width)).is(frame)) {
            return null;
        }

        int height = 0;
        cursor.set(bottom);
        while (height < 21 && isEmpty(level, cursor)) {
            height++;
            cursor.move(Direction.UP);
        }
        if (height < 3 || height > 21 || !level.getBlockState(bottom.above(height)).is(frame)) {
            return null;
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isEmpty(level, bottom.relative(right, x).above(y))) {
                    return null;
                }
            }
            if (!level.getBlockState(bottom.relative(right, x).below()).is(frame)) {
                return null;
            }
            if (!level.getBlockState(bottom.relative(right, x).above(height)).is(frame)) {
                return null;
            }
        }
        for (int y = 0; y < height; y++) {
            if (!level.getBlockState(bottom.relative(left).above(y)).is(frame)) {
                return null;
            }
            if (!level.getBlockState(bottom.relative(right, width).above(y)).is(frame)) {
                return null;
            }
        }
        return new PortalFrame(bottom.immutable(), axis, width, height);
    }

    private static void fillPortal(Level level, PortalFrame frame, ModPortalBlock portal) {
        Direction right = Direction.fromAxisAndDirection(frame.axis, Direction.AxisDirection.POSITIVE);
        for (int x = 0; x < frame.width; x++) {
            for (int y = 0; y < frame.height; y++) {
                BlockPos pos = frame.bottomLeft.relative(right, x).above(y);
                // Flag 2|16 avoids neighbor updates destroying partial portal mid-place
                level.setBlock(pos, portal.defaultBlockState().setValue(AXIS, frame.axis), 2 | 16);
            }
        }
        // Now notify neighbors once the full portal exists
        for (int x = 0; x < frame.width; x++) {
            for (int y = 0; y < frame.height; y++) {
                BlockPos pos = frame.bottomLeft.relative(right, x).above(y);
                level.blockUpdated(pos, portal);
            }
        }
    }

    private static boolean isEmpty(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.getBlock() instanceof ModPortalBlock;
    }

    private record PortalFrame(BlockPos bottomLeft, Direction.Axis axis, int width, int height) {}
}
