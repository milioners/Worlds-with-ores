package net.millioners.worldswithores.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.millioners.worldswithores.block.FluxCoilBlock;
import net.millioners.worldswithores.block.FluxCoilTier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Places Flux Coils with the tier stored in BlockStateTag / custom NBT.
 */
public class FluxCoilBlockItem extends FluxTooltipBlockItem {
    public FluxCoilBlockItem(Block block, Properties properties, String hintKey) {
        super(block, properties, hintKey);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        FluxCoilTier tier = getTier(context.getItemInHand());
        if (state.hasProperty(FluxCoilBlock.TIER)) {
            state = state.setValue(FluxCoilBlock.TIER, tier);
        }
        return super.placeBlock(context, state);
    }

    @Override
    public Component getName(ItemStack stack) {
        FluxCoilTier tier = getTier(stack);
        if (tier == FluxCoilTier.BASIC) {
            return super.getName(stack);
        }
        return Component.translatable(this.getDescriptionId(stack))
                .append(Component.literal(" (" + tier.getSerializedName().toUpperCase() + ")"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.worlds_with_ores.flux_coil.tier",
                getTier(stack).getSerializedName().toUpperCase()).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.worlds_with_ores.flux_coil.craft_upgrade")
                .withStyle(ChatFormatting.GRAY));
    }

    public static FluxCoilTier getTier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (tag.contains("Tier", CompoundTag.TAG_INT)) {
                int index = tag.getInt("Tier");
                FluxCoilTier[] values = FluxCoilTier.values();
                return values[Math.max(0, Math.min(values.length - 1, index))];
            }
            if (tag.contains("BlockStateTag", CompoundTag.TAG_COMPOUND)) {
                CompoundTag stateTag = tag.getCompound("BlockStateTag");
                if (stateTag.contains("tier", CompoundTag.TAG_STRING)) {
                    String name = stateTag.getString("tier");
                    for (FluxCoilTier tier : FluxCoilTier.values()) {
                        if (tier.getSerializedName().equals(name)) {
                            return tier;
                        }
                    }
                }
            }
        }
        return FluxCoilTier.BASIC;
    }

    public static ItemStack withTier(ItemStack stack, FluxCoilTier tier) {
        ItemStack result = stack.copy();
        result.setCount(1);
        CompoundTag tag = result.getOrCreateTag();
        tag.putInt("Tier", tier.ordinal());
        CompoundTag stateTag = new CompoundTag();
        stateTag.putString("tier", tier.getSerializedName());
        tag.put("BlockStateTag", stateTag);
        return result;
    }
}
