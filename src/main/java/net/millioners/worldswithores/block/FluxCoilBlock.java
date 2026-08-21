package net.millioners.worldswithores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.millioners.worldswithores.blockentity.FluxMultiblock;
import net.millioners.worldswithores.registry.ModItems;

public class FluxCoilBlock extends Block {
    public static final EnumProperty<FluxCoilTier> TIER = EnumProperty.create("tier", FluxCoilTier.class);

    public FluxCoilBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .requiresCorrectToolForDrops()
                .strength(4.0F, 12.0F)
                .sound(SoundType.COPPER)
                .lightLevel(state -> 4));
        registerDefaultState(stateDefinition.any().setValue(TIER, FluxCoilTier.BASIC));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        FluxCoilTier tier = state.getValue(TIER);

        if (held.is(ModItems.FLUX_COIL_UPGRADE_ADVANCED.get()) || held.is(ModItems.FLUX_COIL_UPGRADE_QUANTUM.get())) {
            boolean validUpgrade = (tier == FluxCoilTier.BASIC && held.is(ModItems.FLUX_COIL_UPGRADE_ADVANCED.get()))
                    || (tier == FluxCoilTier.ADVANCED && held.is(ModItems.FLUX_COIL_UPGRADE_QUANTUM.get()));
            if (!level.isClientSide) {
                if (validUpgrade) {
                    FluxCoilTier next = tier.next();
                    level.setBlock(pos, state.setValue(TIER, next), Block.UPDATE_ALL);
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    player.displayClientMessage(Component.translatable(
                            "message.worlds_with_ores.coil.upgraded", next.getSerializedName().toUpperCase()), true);
                } else if (tier == FluxCoilTier.QUANTUM) {
                    player.displayClientMessage(Component.translatable("message.worlds_with_ores.coil.max"), true);
                } else if (tier == FluxCoilTier.BASIC) {
                    player.displayClientMessage(Component.translatable("message.worlds_with_ores.coil.need_advanced"), true);
                } else {
                    player.displayClientMessage(Component.translatable("message.worlds_with_ores.coil.need_quantum"), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        boolean opened = FluxMultiblock.openController(level, pos, player);
        return opened ? InteractionResult.CONSUME : InteractionResult.sidedSuccess(level.isClientSide);
    }
}
