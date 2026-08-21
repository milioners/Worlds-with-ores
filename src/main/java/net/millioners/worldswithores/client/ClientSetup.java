package net.millioners.worldswithores.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.registry.ModBlockEntities;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModMenus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WorldsWithOresMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.CHEST.get(), ModChestScreen::new);
            MenuScreens.register(ModMenus.FLUX_CONTROLLER.get(), FluxControllerScreen::new);
            MenuScreens.register(ModMenus.FLUX_BATTERY.get(), FluxBatteryScreen::new);
            MenuScreens.register(ModMenus.FLUX_CHARGER.get(), FluxChargerScreen::new);
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
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLUX_COIL.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLUX_GLASS.get(), RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FLUX_CONTROLLER.get(), FluxControllerRenderer::new);
    }

    private static void setPortalLayer(net.minecraft.world.level.block.Block portal) {
        ItemBlockRenderTypes.setRenderLayer(portal, RenderType.translucent());
    }
}
