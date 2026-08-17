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
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.COALWORD_PORTAL.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRONWORLD_PORTAL.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLDWORLD_PORTAL.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIAMONDWORLD_PORTAL.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.EMERALDWORLD_PORTAL.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LAPISWORLD_PORTAL.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REDSTONEWORLD_PORTAL.get(), RenderType.translucent());
        });
    }
}
