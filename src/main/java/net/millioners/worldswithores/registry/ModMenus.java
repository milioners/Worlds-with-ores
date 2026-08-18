package net.millioners.worldswithores.registry;

import net.minecraft.world.inventory.MenuType;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.menu.FluxBatteryMenu;
import net.millioners.worldswithores.menu.FluxChargerMenu;
import net.millioners.worldswithores.menu.FluxControllerMenu;
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

    public static final RegistryObject<MenuType<FluxControllerMenu>> FLUX_CONTROLLER =
            MENUS.register("flux_controller", () -> IForgeMenuType.create(FluxControllerMenu::new));

    public static final RegistryObject<MenuType<FluxBatteryMenu>> FLUX_BATTERY =
            MENUS.register("flux_battery", () -> IForgeMenuType.create(FluxBatteryMenu::new));

    public static final RegistryObject<MenuType<FluxChargerMenu>> FLUX_CHARGER =
            MENUS.register("flux_charger", () -> IForgeMenuType.create(FluxChargerMenu::new));
}
