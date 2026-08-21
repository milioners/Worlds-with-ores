package net.millioners.worldswithores.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.blockentity.FluxBatteryBlockEntity;
import net.millioners.worldswithores.blockentity.FluxChargerBlockEntity;
import net.millioners.worldswithores.blockentity.FluxControllerBlockEntity;
import net.millioners.worldswithores.blockentity.FluxEnergyPortBlockEntity;
import net.millioners.worldswithores.blockentity.ModChestBlockEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WorldsWithOresMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ModChestBlockEntity>> CHEST =
            BLOCK_ENTITIES.register("chest",
                    () -> BlockEntityType.Builder.of(ModChestBlockEntity::new, ModBlocks.CHEST.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluxControllerBlockEntity>> FLUX_CONTROLLER =
            BLOCK_ENTITIES.register("flux_controller",
                    () -> BlockEntityType.Builder.of(FluxControllerBlockEntity::new, ModBlocks.FLUX_CONTROLLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluxEnergyPortBlockEntity>> FLUX_ENERGY_PORT =
            BLOCK_ENTITIES.register("flux_energy_port",
                    () -> BlockEntityType.Builder.of(FluxEnergyPortBlockEntity::new, ModBlocks.FLUX_ENERGY_PORT.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluxBatteryBlockEntity>> FLUX_BATTERY =
            BLOCK_ENTITIES.register("flux_battery",
                    () -> BlockEntityType.Builder.of(FluxBatteryBlockEntity::new, ModBlocks.FLUX_BATTERY.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluxChargerBlockEntity>> FLUX_CHARGER =
            BLOCK_ENTITIES.register("flux_charger",
                    () -> BlockEntityType.Builder.of(FluxChargerBlockEntity::new, ModBlocks.FLUX_CHARGER.get()).build(null));
}
