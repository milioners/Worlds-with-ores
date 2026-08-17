package net.millioners.worldswithores;

import net.millioners.worldswithores.registry.ModBlockEntities;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModCreativeTabs;
import net.millioners.worldswithores.registry.ModFeatures;
import net.millioners.worldswithores.registry.ModItems;
import net.millioners.worldswithores.registry.ModMenus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WorldsWithOresMod.MOD_ID)
public class WorldsWithOresMod {
    public static final String MOD_ID = "worlds_with_ores";
    public static final Logger LOGGER = LogManager.getLogger();

    public WorldsWithOresMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModCreativeTabs.CREATIVE_TABS.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
