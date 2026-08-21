package net.millioners.worldswithores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.millioners.worldswithores.blockentity.FluxControllerBlockEntity;
import net.millioners.worldswithores.registry.ModBlockEntities;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class FluxControllerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public FluxControllerBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 16.0F)
                .sound(SoundType.METAL)
                .lightLevel(state -> state.getValue(LIT) ? 12 : 4));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(FORMED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, FORMED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluxControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.FLUX_CONTROLLER.get(), FluxControllerBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FluxControllerBlockEntity controller && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, controller, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(FORMED)) {
            return;
        }
        Direction inward = state.getValue(FACING).getOpposite();
        double x = pos.getX() + 0.5D + inward.getStepX() * 2.0D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D + inward.getStepZ() * 2.0D;
        boolean active = state.getValue(LIT);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean overheated = blockEntity instanceof FluxControllerBlockEntity controller && controller.isOverheated();
        int count = active ? 5 : 2;
        for (int i = 0; i < count; i++) {
            level.addParticle(overheated ? ParticleTypes.FLAME : ParticleTypes.ELECTRIC_SPARK,
                    x + (random.nextDouble() - 0.5D) * 0.6D,
                    y + (random.nextDouble() - 0.5D) * 0.6D,
                    z + (random.nextDouble() - 0.5D) * 0.6D,
                    (random.nextDouble() - 0.5D) * 0.04D,
                    random.nextDouble() * 0.04D,
                    (random.nextDouble() - 0.5D) * 0.04D);
        }
        if (active && random.nextFloat() < 0.55F) {
            level.addParticle(ParticleTypes.ENCHANT, x, y + 0.4D, z,
                    (random.nextDouble() - 0.5D) * 0.8D, 0.3D, (random.nextDouble() - 0.5D) * 0.8D);
        }
        if (active && random.nextFloat() < 0.45F) {
            boolean sideCoil = random.nextBoolean();
            int sign = random.nextBoolean() ? 2 : -2;
            Direction side = state.getValue(FACING).getClockWise();
            double sourceX = sideCoil ? x + side.getStepX() * sign : x;
            double sourceY = sideCoil ? y : y + sign;
            double sourceZ = sideCoil ? z + side.getStepZ() * sign : z;
            level.addParticle(ParticleTypes.END_ROD, sourceX, sourceY, sourceZ,
                    (x - sourceX) * 0.08D, (y - sourceY) * 0.08D, (z - sourceZ) * 0.08D);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FluxControllerBlockEntity controller) {
                controller.dropContents(level, pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
