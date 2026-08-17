package net.millioners.worldswithores.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.registry.ModItems;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldsWithOresMod.MOD_ID)
public final class ModPlayerEvents {
    private static final String STARTER_BOOK_KEY = "worlds_with_ores_starter_book";

    private ModPlayerEvents() {}

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (persisted.getBoolean(STARTER_BOOK_KEY)) {
            return;
        }

        persisted.putBoolean(STARTER_BOOK_KEY, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);

        ItemStack book = new ItemStack(ModItems.RECIPES_BOOK.get());
        if (!player.addItem(book)) {
            player.drop(book, false);
        }
        player.displayClientMessage(Component.translatable("message.worlds_with_ores.starter_book"), false);
    }
}
