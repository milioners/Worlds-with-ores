package net.millioners.worldswithores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.millioners.worldswithores.blockentity.FluxWirelessBlockEntity;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class FluxWirelessBlock extends BaseEntityBlock {
    private final boolean transmitter;

    public FluxWirelessBlock(boolean transmitter) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .requiresCorrectToolForDrops()
                .strength(3.5F, 10.0F)
                .sound(SoundType.METAL)
                .lightLevel(state -> 6));
        this.transmitter = transmitter;
    }

    public boolean isTransmitter() {
        return this.transmitter;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluxWirelessBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.FLUX_WIRELESS.get(),
                FluxWirelessBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof FluxWirelessBlockEntity wireless) {
            player.displayClientMessage(wireless.statusMessage(), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof FluxWirelessBlockEntity wireless) {
            BlockPos linked = wireless.getLinkedPos();
            if (linked != null && level.getBlockEntity(linked) instanceof FluxWirelessBlockEntity partner) {
                partner.setLinkedPos(null);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
