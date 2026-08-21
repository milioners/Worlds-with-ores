package net.millioners.worldswithores.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.millioners.worldswithores.block.FluxCoilTier;
import net.millioners.worldswithores.blockentity.FluxMultiblock;
import net.millioners.worldswithores.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Coil upgrade item that applies on use (does not rely only on block.use).
 */
public class FluxCoilUpgradeItem extends FluxTooltipItem {
    private final FluxCoilTier fromTier;

    public FluxCoilUpgradeItem(Properties properties, String hintKey, FluxCoilTier fromTier) {
        super(properties, hintKey);
        this.fromTier = fromTier;
    }

    public FluxCoilTier fromTier() {
        return this.fromTier;
    }

    public FluxCoilTier toTier() {
        return this.fromTier.next();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(context.getClickedPos());
        boolean reactorPart = state.is(ModBlocks.FLUX_COIL.get())
                || state.is(ModBlocks.FLUX_CASING.get())
                || state.is(ModBlocks.FLUX_GLASS.get())
                || state.is(ModBlocks.FLUX_CONTROLLER.get())
                || state.is(ModBlocks.FLUX_ENERGY_PORT.get());
        if (!reactorPart) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            FluxMultiblock.applyCoilUpgrade(
                    level,
                    context.getClickedPos(),
                    player,
                    context.getItemInHand(),
                    player.isShiftKeyDown());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.worlds_with_ores.flux_coil_upgrade.craft")
                .withStyle(ChatFormatting.DARK_AQUA));
    }
}
