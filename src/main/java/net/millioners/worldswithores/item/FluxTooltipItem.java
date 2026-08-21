package net.millioners.worldswithores.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Flux item with optional usage hint (no WIP badge). */
public class FluxTooltipItem extends Item {
    private final String hintKey;

    public FluxTooltipItem(Properties properties, String hintKey) {
        super(properties);
        this.hintKey = hintKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (this.hintKey != null && !this.hintKey.isEmpty()) {
            tooltip.add(Component.translatable(this.hintKey).withStyle(ChatFormatting.GRAY));
        }
    }
}
