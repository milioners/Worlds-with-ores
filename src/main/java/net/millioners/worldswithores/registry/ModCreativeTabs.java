package net.millioners.worldswithores.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WorldsWithOresMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_TABS.register("worldswithores",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tabworldswithores"))
                    .icon(() -> new ItemStack(ModItems.DIAMONDWORLD.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.CATALYST_ORE_COAL.get());
                        output.accept(ModItems.CATALYST_ORE_IRON.get());
                        output.accept(ModItems.CATALYST_ORE_GOLD.get());
                        output.accept(ModItems.CATALYST_ORE_DIAMOND.get());
                        output.accept(ModItems.CATALYST_ORE_EMERALD.get());
                        output.accept(ModItems.CATALYST_ORE_LAPIS.get());
                        output.accept(ModItems.CATALYST_ORE_REDSTONE.get());
                        output.accept(ModItems.CATALYST_COAL.get());
                        output.accept(ModItems.CATALYST_IRON.get());
                        output.accept(ModItems.CATALYST_GOLD.get());
                        output.accept(ModItems.CATALYST_DIAMOND.get());
                        output.accept(ModItems.CATALYST_EMERALD.get());
                        output.accept(ModItems.CATALYST_LAPIS.get());
                        output.accept(ModItems.CATALYST_REDSTONE.get());
                        output.accept(ModItems.INGOT_COAL.get());
                        output.accept(ModItems.INGOT_IRON.get());
                        output.accept(ModItems.INGOT_GOLD.get());
                        output.accept(ModItems.INGOT_DIAMOND.get());
                        output.accept(ModItems.INGOT_EMERALD.get());
                        output.accept(ModItems.INGOT_LAPIS.get());
                        output.accept(ModItems.INGOT_REDSTONE.get());
                        output.accept(ModItems.COALWORD.get());
                        output.accept(ModItems.IRONWORLD.get());
                        output.accept(ModItems.GOLDWORLD.get());
                        output.accept(ModItems.DIAMONDWORLD.get());
                        output.accept(ModItems.EMERALDWORLD.get());
                        output.accept(ModItems.LAPISWORLD.get());
                        output.accept(ModItems.REDSTONEWORLD.get());

                        if (ModList.get().isLoaded("create")) {
                            output.accept(ModItems.ZINCWORLD.get());
                        }
                        if (ModList.get().isLoaded("mekanism")) {
                            output.accept(ModItems.OSMIUMWORLD.get());
                        }
                        if (ModList.get().isLoaded("immersiveengineering")) {
                            output.accept(ModItems.ALUMINUMWORLD.get());
                        }
                        if (ModList.get().isLoaded("thermal")) {
                            output.accept(ModItems.SILVERWORLD.get());
                        }
                        if (ModList.get().isLoaded("bigreactors")) {
                            output.accept(ModItems.YELLORIUMWORLD.get());
                        }
                        if (ModList.get().isLoaded("ae2")) {
                            output.accept(ModItems.CERTUSWORLD.get());
                        }

                        output.accept(ModItems.NETHERBRICKBRINSTAR.get());
                        output.accept(ModItems.NETHERBRICKCLASSICSPATTER.get());
                        output.accept(ModItems.NETHERBRICKGUTS.get());
                        output.accept(ModItems.NETHERBRICKLAVABROWN.get());
                        output.accept(ModItems.NETHERBRICKLAVAOBSIDIAN.get());
                        output.accept(ModItems.NETHERBRICKLAVASTONEDARK.get());
                        output.accept(ModItems.CHEST.get());
                        output.accept(ModItems.RECIPES_BOOK.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_SWORD.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_PICKAXE.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_AXE.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_SHOVEL.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_HOE.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_HELMET.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_CHESTPLATE.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_LEGGINGS.get());
                        output.accept(ModItems.LAVA_OBSIDIAN_BOOTS.get());
                    })
                    .build());
}
