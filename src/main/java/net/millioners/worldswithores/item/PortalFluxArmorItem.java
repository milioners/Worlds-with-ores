package net.millioners.worldswithores.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.millioners.worldswithores.energy.FluxPoweredItems;
import net.millioners.worldswithores.energy.ItemEnergy;
import net.millioners.worldswithores.registry.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PortalFluxArmorItem extends ArmorItem {
    public static final int CAPACITY = 400_000;
    public static final int TRANSFER = 2_000;

    public PortalFluxArmorItem(Type type) {
        super(ModArmorMaterials.PORTAL_FLUX, type, new Properties().stacksTo(1).fireResistant());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return ItemEnergy.provider(stack, CAPACITY, TRANSFER);
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
        tip.add(Component.translatable("tooltip.worlds_with_ores.portal_flux_armor." + this.type.getName()));
        tip.add(Component.translatable("tooltip.worlds_with_ores.portal_flux_armor.set").withStyle(net.minecraft.ChatFormatting.AQUA));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public static boolean isFluxArmor(ItemStack stack) {
        return stack.getItem() instanceof PortalFluxArmorItem;
    }

    public static boolean hasFullSet(Player player) {
        return isFluxArmor(player.getItemBySlot(EquipmentSlot.HEAD))
                && isFluxArmor(player.getItemBySlot(EquipmentSlot.CHEST))
                && isFluxArmor(player.getItemBySlot(EquipmentSlot.LEGS))
                && isFluxArmor(player.getItemBySlot(EquipmentSlot.FEET));
    }

    public static ItemStack piece(Player player, EquipmentSlot slot) {
        return player.getItemBySlot(slot);
    }

    public static boolean consumeFrom(ItemStack stack, int amount) {
        return FluxPoweredItems.tryConsume(stack, amount);
    }

    public static boolean isOurArmorItem(ItemStack stack) {
        return stack.is(ModItems.PORTAL_FLUX_HELMET.get())
                || stack.is(ModItems.PORTAL_FLUX_CHESTPLATE.get())
                || stack.is(ModItems.PORTAL_FLUX_LEGGINGS.get())
                || stack.is(ModItems.PORTAL_FLUX_BOOTS.get());
    }
}
