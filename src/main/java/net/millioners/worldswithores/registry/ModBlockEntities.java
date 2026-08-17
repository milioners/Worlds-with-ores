package net.millioners.worldswithores.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.millioners.worldswithores.WorldsWithOresMod;
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
}
