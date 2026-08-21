package net.millioners.worldswithores.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.millioners.worldswithores.energy.FluxPoweredItems;
import net.millioners.worldswithores.energy.ItemEnergy;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class PortalFluxTools {
    public static final int CAPACITY = 100_000;
    public static final int MINE_COST = 40;
    public static final int HIT_COST = 60;

    private PortalFluxTools() {}

    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(ModTiers.LAVA_OBSIDIAN, 1, -2.8F, new Properties().stacksTo(1).fireResistant());
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return ItemEnergy.provider(stack, CAPACITY, 1_000);
        }

        @Override
        public boolean mineBlock(ItemStack stack, Level level, BlockState state, net.minecraft.core.BlockPos pos, LivingEntity entity) {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                if (!FluxPoweredItems.tryConsume(stack, MINE_COST)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public float getDestroySpeed(ItemStack stack, BlockState state) {
            return FluxPoweredItems.hasEnergy(stack, MINE_COST) ? super.getDestroySpeed(stack, state) : 0.5F;
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return true;
        }

        @Override
        public int getBarWidth(ItemStack stack) {
            return FluxPoweredItems.barWidth(stack, CAPACITY);
        }

        @Override
        public int getBarColor(ItemStack stack) {
            return FluxPoweredItems.barColor();
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
            FluxPoweredItems.appendEnergyTooltip(stack, CAPACITY, tip);
        }

        @Override
        public boolean isDamageable(ItemStack stack) {
            return false;
        }
    }

    public static class Axe extends AxeItem {
        public Axe() {
            super(ModTiers.LAVA_OBSIDIAN, 5.0F, -3.0F, new Properties().stacksTo(1).fireResistant());
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return ItemEnergy.provider(stack, CAPACITY, 1_000);
        }

        @Override
        public boolean mineBlock(ItemStack stack, Level level, BlockState state, net.minecraft.core.BlockPos pos, LivingEntity entity) {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                if (!FluxPoweredItems.tryConsume(stack, MINE_COST)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public float getDestroySpeed(ItemStack stack, BlockState state) {
            return FluxPoweredItems.hasEnergy(stack, MINE_COST) ? super.getDestroySpeed(stack, state) : 0.5F;
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return true;
        }

        @Override
        public int getBarWidth(ItemStack stack) {
            return FluxPoweredItems.barWidth(stack, CAPACITY);
        }

        @Override
        public int getBarColor(ItemStack stack) {
            return FluxPoweredItems.barColor();
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
            FluxPoweredItems.appendEnergyTooltip(stack, CAPACITY, tip);
        }

        @Override
        public boolean isDamageable(ItemStack stack) {
            return false;
        }
    }

    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(ModTiers.LAVA_OBSIDIAN, 1.5F, -3.0F, new Properties().stacksTo(1).fireResistant());
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return ItemEnergy.provider(stack, CAPACITY, 1_000);
        }

        @Override
        public boolean mineBlock(ItemStack stack, Level level, BlockState state, net.minecraft.core.BlockPos pos, LivingEntity entity) {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                if (!FluxPoweredItems.tryConsume(stack, MINE_COST)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public float getDestroySpeed(ItemStack stack, BlockState state) {
            return FluxPoweredItems.hasEnergy(stack, MINE_COST) ? super.getDestroySpeed(stack, state) : 0.5F;
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return true;
        }

        @Override
        public int getBarWidth(ItemStack stack) {
            return FluxPoweredItems.barWidth(stack, CAPACITY);
        }

        @Override
        public int getBarColor(ItemStack stack) {
            return FluxPoweredItems.barColor();
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
            FluxPoweredItems.appendEnergyTooltip(stack, CAPACITY, tip);
        }

        @Override
        public boolean isDamageable(ItemStack stack) {
            return false;
        }
    }

    public static class Hoe extends HoeItem {
        public Hoe() {
            super(ModTiers.LAVA_OBSIDIAN, -4, 0.0F, new Properties().stacksTo(1).fireResistant());
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return ItemEnergy.provider(stack, CAPACITY, 1_000);
        }

        @Override
        public boolean mineBlock(ItemStack stack, Level level, BlockState state, net.minecraft.core.BlockPos pos, LivingEntity entity) {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                if (!FluxPoweredItems.tryConsume(stack, MINE_COST)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public float getDestroySpeed(ItemStack stack, BlockState state) {
            return FluxPoweredItems.hasEnergy(stack, MINE_COST) ? super.getDestroySpeed(stack, state) : 0.5F;
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return true;
        }

        @Override
        public int getBarWidth(ItemStack stack) {
            return FluxPoweredItems.barWidth(stack, CAPACITY);
        }

        @Override
        public int getBarColor(ItemStack stack) {
            return FluxPoweredItems.barColor();
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
            FluxPoweredItems.appendEnergyTooltip(stack, CAPACITY, tip);
        }

        @Override
        public boolean isDamageable(ItemStack stack) {
            return false;
        }
    }

    public static class Sword extends SwordItem {
        public Sword() {
            super(ModTiers.LAVA_OBSIDIAN, 3, -2.0F, new Properties().stacksTo(1).fireResistant());
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return ItemEnergy.provider(stack, CAPACITY, 1_000);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            if (!attacker.level().isClientSide) {
                FluxPoweredItems.tryConsume(stack, HIT_COST);
            }
            return true;
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return true;
        }

        @Override
        public int getBarWidth(ItemStack stack) {
            return FluxPoweredItems.barWidth(stack, CAPACITY);
        }

        @Override
        public int getBarColor(ItemStack stack) {
            return FluxPoweredItems.barColor();
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
            FluxPoweredItems.appendEnergyTooltip(stack, CAPACITY, tip);
            tip.add(Component.translatable("tooltip.worlds_with_ores.portal_flux_sword"));
        }

        @Override
        public boolean isDamageable(ItemStack stack) {
            return false;
        }
    }
}
