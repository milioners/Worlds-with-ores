package net.millioners.worldswithores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.millioners.worldswithores.blockentity.FluxMultiblock;

public class FluxGlassBlock extends GlassBlock {
    public FluxGlassBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 12.0F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .lightLevel(state -> 5));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        boolean opened = FluxMultiblock.openController(level, pos, player);
        return opened ? InteractionResult.CONSUME : InteractionResult.sidedSuccess(level.isClientSide);
    }
}
