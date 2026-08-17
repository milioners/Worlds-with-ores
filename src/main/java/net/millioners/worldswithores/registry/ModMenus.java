package net.millioners.worldswithores.registry;

import net.minecraft.world.inventory.MenuType;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.menu.ModChestMenu;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, WorldsWithOresMod.MOD_ID);

    public static final RegistryObject<MenuType<ModChestMenu>> CHEST =
            MENUS.register("chest", () -> IForgeMenuType.create(ModChestMenu::new));
}
