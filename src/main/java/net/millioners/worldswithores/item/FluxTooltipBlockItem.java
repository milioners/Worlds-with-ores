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

/** Block item marked as Portal Flux work-in-progress. */
public class FluxTooltipBlockItem extends BlockItem {
    private final String hintKey;

    public FluxTooltipBlockItem(Block block, Properties properties, String hintKey) {
        super(block, properties);
        this.hintKey = hintKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.worlds_with_ores.energy_wip").withStyle(ChatFormatting.GOLD));
        if (this.hintKey != null && !this.hintKey.isEmpty()) {
            tooltip.add(Component.translatable(this.hintKey).withStyle(ChatFormatting.GRAY));
        }
    }
}
