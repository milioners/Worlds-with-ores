package net.millioners.worldswithores.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.millioners.worldswithores.energy.ItemEnergy;
import net.millioners.worldswithores.item.ModTiers;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PortalFluxPickaxeItem extends PickaxeItem {
    public static final int CAPACITY = 100_000;
    public static final int COST = 40;

    public PortalFluxPickaxeItem() {
        super(ModTiers.LAVA_OBSIDIAN, 1, -2.8F, new Properties().stacksTo(1).fireResistant());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return ItemEnergy.provider(stack, CAPACITY, 1_000);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, net.minecraft.core.BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            if (ItemEnergy.getEnergy(stack) < COST) {
                return false;
            }
            ItemEnergy.extract(stack, COST, COST, false);
        }
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (ItemEnergy.getEnergy(stack) < COST) {
            return 0.5F;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * ItemEnergy.getEnergy(stack) / (float) CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x6FA8C8;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.translatable("item.worlds_with_ores.portal_flux_pickaxe.energy",
                ItemEnergy.getEnergy(stack), CAPACITY));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}
