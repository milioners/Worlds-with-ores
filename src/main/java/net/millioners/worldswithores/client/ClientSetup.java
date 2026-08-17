package net.millioners.worldswithores.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModMenus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WorldsWithOresMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.CHEST.get(), ModChestScreen::new);
            setPortalLayer(ModBlocks.COALWORD_PORTAL.get());
            setPortalLayer(ModBlocks.IRONWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.GOLDWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.DIAMONDWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.EMERALDWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.LAPISWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.REDSTONEWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.ZINCWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.OSMIUMWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.ALUMINUMWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.SILVERWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.YELLORIUMWORLD_PORTAL.get());
            setPortalLayer(ModBlocks.CERTUSWORLD_PORTAL.get());
        });
    }

    private static void setPortalLayer(net.minecraft.world.level.block.Block portal) {
        ItemBlockRenderTypes.setRenderLayer(portal, RenderType.translucent());
    }
}
