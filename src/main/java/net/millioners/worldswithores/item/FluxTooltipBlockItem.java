package net.millioners.worldswithores.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Flux block item with optional usage hint (no WIP badge). */
public class FluxTooltipBlockItem extends BlockItem {
    private final String hintKey;

    public FluxTooltipBlockItem(Block block, Properties properties, String hintKey) {
        super(block, properties);
        this.hintKey = hintKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (this.hintKey != null && !this.hintKey.isEmpty()) {
            tooltip.add(Component.translatable(this.hintKey).withStyle(ChatFormatting.GRAY));
        }
    }
}
