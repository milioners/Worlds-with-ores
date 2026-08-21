package net.millioners.worldswithores.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.item.PortalFluxArmorItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldsWithOresMod.MOD_ID)
public final class FluxArmorEvents {
    private static final int HELMET_COST = 20;
    private static final int LEGS_COST = 15;
    private static final int CHEST_COST = 800;
    private static final int BOOTS_COST_PER_BLOCK = 40;
    private static final int MAGNET_COST = 10;

    private FluxArmorEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        Player player = event.player;
        if (player.tickCount % 20 == 0) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (PortalFluxArmorItem.isFluxArmor(helmet) && PortalFluxArmorItem.consumeFrom(helmet, HELMET_COST)) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false, true));
            }
            ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
            if (PortalFluxArmorItem.isFluxArmor(legs) && player.getDeltaMovement().horizontalDistanceSqr() > 0.001
                    && PortalFluxArmorItem.consumeFrom(legs, LEGS_COST)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, false, true));
            }
        }
        if (PortalFluxArmorItem.hasFullSet(player) && player.tickCount % 10 == 0) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (!PortalFluxArmorItem.consumeFrom(chest, MAGNET_COST)) {
                return;
            }
            AABB box = player.getBoundingBox().inflate(5.0D);
            List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, box);
            for (ItemEntity item : items) {
                Vec3 to = player.position().add(0.0D, 0.5D, 0.0D).subtract(item.position());
                if (to.lengthSqr() < 0.01D) {
                    continue;
                }
                item.setDeltaMovement(item.getDeltaMovement().add(to.normalize().scale(0.25D)));
                item.hasImpulse = true;
            }
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!PortalFluxArmorItem.isFluxArmor(chest)) {
            return;
        }
        if (PortalFluxArmorItem.consumeFrom(chest, CHEST_COST)) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1, true, false, true));
            event.setAmount(event.getAmount() * 0.75F);
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!PortalFluxArmorItem.isFluxArmor(boots)) {
            return;
        }
        int cost = Math.max(BOOTS_COST_PER_BLOCK, (int) (event.getDistance() * BOOTS_COST_PER_BLOCK));
        if (PortalFluxArmorItem.consumeFrom(boots, cost)) {
            event.setCanceled(true);
        }
    }
}
