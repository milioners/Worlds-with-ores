package net.millioners.worldswithores.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModItems;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class ModRecipePages {
    public enum Category {
        GUIDE("gui.worlds_with_ores.recipe_book.guide"),
        MATERIALS("gui.worlds_with_ores.recipe_book.materials"),
        PORTALS("gui.worlds_with_ores.recipe_book.portals"),
        COMPAT("gui.worlds_with_ores.recipe_book.compat"),
        TOOLS("gui.worlds_with_ores.recipe_book.tools"),
        ARMOR("gui.worlds_with_ores.recipe_book.armor"),
        BLOCKS("gui.worlds_with_ores.recipe_book.blocks");

        public final String titleKey;

        Category(String titleKey) {
            this.titleKey = titleKey;
        }
    }

    public enum Kind {
        CRAFTING,
        SMELTING,
        INFO,
        PORTAL
    }

    public record Page(
            Component title,
            Kind kind,
            ItemStack[] pattern,
            ItemStack result,
            Component hint,
            ItemStack frameBlock,
            ItemStack portalBlock
    ) {
        public static Page crafting(String titleKey, ItemStack[] pattern, ItemStack result) {
            return new Page(Component.translatable(titleKey), Kind.CRAFTING, pattern, result,
                    Component.empty(), ItemStack.EMPTY, ItemStack.EMPTY);
        }

        public static Page crafting(String titleKey, ItemStack[] pattern, ItemStack result, String hintKey) {
            return new Page(Component.translatable(titleKey), Kind.CRAFTING, pattern, result,
                    Component.translatable(hintKey), ItemStack.EMPTY, ItemStack.EMPTY);
        }

        public static Page smelting(String titleKey, ItemStack input, ItemStack output, String hintKey) {
            return new Page(Component.translatable(titleKey), Kind.SMELTING, new ItemStack[]{input}, output,
                    Component.translatable(hintKey), ItemStack.EMPTY, ItemStack.EMPTY);
        }

        public static Page info(String titleKey, ItemStack[] icons, String hintKey) {
            return new Page(Component.translatable(titleKey), Kind.INFO, icons, ItemStack.EMPTY,
                    Component.translatable(hintKey), ItemStack.EMPTY, ItemStack.EMPTY);
        }

        public static Page portal(String titleKey, ItemLike frame, ItemLike portal, ItemLike igniter, String hintKey) {
            ItemStack portalStack = new ItemStack(portal.asItem());
            if (portalStack.isEmpty()) {
                portalStack = new ItemStack(Items.PURPLE_STAINED_GLASS);
            }
            return new Page(Component.translatable(titleKey), Kind.PORTAL, new ItemStack[0], i(igniter),
                    Component.translatable(hintKey), i(frame), portalStack);
        }
    }

    private ModRecipePages() {}

    public static List<Page> pagesFor(Category category) {
        return switch (category) {
            case GUIDE -> guide();
            case MATERIALS -> materials();
            case PORTALS -> portals();
            case COMPAT -> compat();
            case TOOLS -> tools();
            case ARMOR -> armor();
            case BLOCKS -> blocks();
        };
    }

    private static ItemStack i(ItemLike item) {
        return new ItemStack(item);
    }

    private static ItemStack[] shaped(ItemLike... cells) {
        ItemStack[] out = new ItemStack[9];
        for (int n = 0; n < 9; n++) {
            out[n] = n < cells.length && cells[n] != null ? i(cells[n]) : ItemStack.EMPTY;
        }
        return out;
    }

    private static ItemStack[] icons(ItemLike... items) {
        ItemStack[] out = new ItemStack[Math.max(1, items.length)];
        for (int n = 0; n < items.length; n++) {
            out[n] = items[n] == null ? ItemStack.EMPTY : i(items[n]);
        }
        return out;
    }

    private static ItemStack optionalItem(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item == null || item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static List<Page> guide() {
        List<Page> list = new ArrayList<>();
        list.add(Page.info("gui.worlds_with_ores.book.guide.portals.title", icons(
                Items.COAL_BLOCK, ModItems.INGOT_COAL.get(), ModItems.COALWORD.get()
        ), "gui.worlds_with_ores.book.guide.portals.hint"));
        list.add(Page.info("gui.worlds_with_ores.book.guide.progress.title", icons(
                ModItems.CATALYST_ORE_COAL.get(),
                ModItems.CATALYST_ORE_IRON.get(),
                ModItems.CATALYST_ORE_GOLD.get(),
                ModItems.CATALYST_ORE_DIAMOND.get()
        ), "gui.worlds_with_ores.book.guide.progress.hint"));
        list.add(Page.info("gui.worlds_with_ores.book.guide.smelt.title", icons(
                ModItems.CATALYST_COAL.get(), Items.FURNACE, ModItems.INGOT_COAL.get()
        ), "gui.worlds_with_ores.book.guide.smelt.hint"));
        return list;
    }

    private static List<Page> materials() {
        List<Page> list = new ArrayList<>();
        list.add(Page.smelting("gui.worlds_with_ores.book.mat.coal", i(ModItems.CATALYST_COAL.get()), i(ModItems.INGOT_COAL.get()),
                "gui.worlds_with_ores.book.mat.coal.hint"));
        list.add(Page.smelting("gui.worlds_with_ores.book.mat.iron", i(ModItems.CATALYST_IRON.get()), i(ModItems.INGOT_IRON.get()),
                "gui.worlds_with_ores.book.mat.iron.hint"));
        list.add(Page.smelting("gui.worlds_with_ores.book.mat.gold", i(ModItems.CATALYST_GOLD.get()), i(ModItems.INGOT_GOLD.get()),
                "gui.worlds_with_ores.book.mat.gold.hint"));
        list.add(Page.smelting("gui.worlds_with_ores.book.mat.diamond", i(ModItems.CATALYST_DIAMOND.get()), i(ModItems.INGOT_DIAMOND.get()),
                "gui.worlds_with_ores.book.mat.diamond.hint"));
        list.add(Page.smelting("gui.worlds_with_ores.book.mat.emerald", i(ModItems.CATALYST_EMERALD.get()), i(ModItems.INGOT_EMERALD.get()),
                "gui.worlds_with_ores.book.mat.emerald.hint"));
        list.add(Page.smelting("gui.worlds_with_ores.book.mat.lapis", i(ModItems.CATALYST_LAPIS.get()), i(ModItems.INGOT_LAPIS.get()),
                "gui.worlds_with_ores.book.mat.lapis.hint"));
        list.add(Page.smelting("gui.worlds_with_ores.book.mat.redstone", i(ModItems.CATALYST_REDSTONE.get()), i(ModItems.INGOT_REDSTONE.get()),
                "gui.worlds_with_ores.book.mat.redstone.hint"));
        return list;
    }

    private static List<Page> portals() {
        List<Page> list = new ArrayList<>();
        addPortal(list, "coal", Blocks.COAL_BLOCK, ModBlocks.COALWORD_PORTAL.get(), ModItems.INGOT_COAL.get(), ModItems.COALWORD.get());
        addPortal(list, "iron", Blocks.IRON_BLOCK, ModBlocks.IRONWORLD_PORTAL.get(), ModItems.INGOT_IRON.get(), ModItems.IRONWORLD.get());
        addPortal(list, "gold", Blocks.GOLD_BLOCK, ModBlocks.GOLDWORLD_PORTAL.get(), ModItems.INGOT_GOLD.get(), ModItems.GOLDWORLD.get());
        addPortal(list, "diamond", Blocks.DIAMOND_BLOCK, ModBlocks.DIAMONDWORLD_PORTAL.get(), ModItems.INGOT_DIAMOND.get(), ModItems.DIAMONDWORLD.get());
        addPortal(list, "emerald", Blocks.EMERALD_BLOCK, ModBlocks.EMERALDWORLD_PORTAL.get(), ModItems.INGOT_EMERALD.get(), ModItems.EMERALDWORLD.get());
        addPortal(list, "lapis", Blocks.LAPIS_BLOCK, ModBlocks.LAPISWORLD_PORTAL.get(), ModItems.INGOT_LAPIS.get(), ModItems.LAPISWORLD.get());
        addPortal(list, "redstone", Blocks.REDSTONE_BLOCK, ModBlocks.REDSTONEWORLD_PORTAL.get(), ModItems.INGOT_REDSTONE.get(), ModItems.REDSTONEWORLD.get());
        return list;
    }

    private static void addPortal(List<Page> list, String id, ItemLike frame, ItemLike portal, ItemLike ingot, ItemLike igniter) {
        list.add(Page.portal(
                "gui.worlds_with_ores.book.portal." + id + ".frame",
                frame, portal, igniter,
                "gui.worlds_with_ores.book.portal." + id + ".frame.hint"
        ));
        list.add(Page.crafting(
                "gui.worlds_with_ores.book.portal." + id + ".igniter",
                shaped(ingot, ingot, ingot, ingot, Items.FLINT, ingot, ingot, ingot, ingot),
                i(igniter),
                "gui.worlds_with_ores.book.portal.igniter.hint"
        ));
    }

    private static List<Page> compat() {
        List<Page> list = new ArrayList<>();
        list.add(Page.info("gui.worlds_with_ores.book.compat.soft.title", icons(
                Items.IRON_PICKAXE, Items.FURNACE, Items.BOOK
        ), "gui.worlds_with_ores.book.compat.soft.hint"));

        addCompatOrePage(list, "create", "gui.worlds_with_ores.book.compat.create_zinc", "create:zinc_ore", "create:raw_zinc");
        addCompatOrePage(list, "mekanism", "gui.worlds_with_ores.book.compat.mek_osmium", "mekanism:osmium_ore", "mekanism:raw_osmium");
        addCompatOrePage(list, "mekanism", "gui.worlds_with_ores.book.compat.mek_tin", "mekanism:tin_ore", "mekanism:raw_tin");
        addCompatOrePage(list, "immersiveengineering", "gui.worlds_with_ores.book.compat.ie_aluminum", "immersiveengineering:ore_aluminum", "immersiveengineering:raw_aluminum");
        addCompatOrePage(list, "thermal", "gui.worlds_with_ores.book.compat.thermal_tin", "thermal:tin_ore", "thermal:tin_ingot");
        addCompatOrePage(list, "bigreactors", "gui.worlds_with_ores.book.compat.yellorite", "bigreactors:yellorite_ore", "bigreactors:yellorium_ingot");
        addCompatOrePage(list, "ae2", "gui.worlds_with_ores.book.compat.ae2", "ae2:quartz_cluster", "ae2:certus_quartz_crystal");

        list.add(Page.info("gui.worlds_with_ores.book.compat.machines.title", icons(
                ModItems.CATALYST_ORE_IRON.get(), ModItems.CATALYST_IRON.get(), ModItems.INGOT_IRON.get()
        ), "gui.worlds_with_ores.book.compat.machines.hint"));
        return list;
    }

    private static void addCompatOrePage(List<Page> list, String modId, String titleKey, String oreId, String productId) {
        if (!ModList.get().isLoaded(modId)) {
            return;
        }
        ItemStack ore = optionalItem(oreId);
        ItemStack product = optionalItem(productId);
        if (ore.isEmpty()) {
            return;
        }
        list.add(Page.info(titleKey, new ItemStack[]{ore, product.isEmpty() ? ItemStack.EMPTY : product},
                "gui.worlds_with_ores.book.compat.ore.hint"));
    }

    private static List<Page> tools() {
        ItemLike m = ModBlocks.NETHERBRICKLAVAOBSIDIAN.get();
        List<Page> list = new ArrayList<>();
        list.add(Page.crafting("gui.worlds_with_ores.book.tool.sword", shaped(m, null, null, m, null, null, Items.STICK, null, null), i(ModItems.LAVA_OBSIDIAN_SWORD.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.tool.pickaxe", shaped(m, m, m, null, Items.STICK, null, null, Items.STICK, null), i(ModItems.LAVA_OBSIDIAN_PICKAXE.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.tool.axe", shaped(m, m, null, m, Items.STICK, null, null, Items.STICK, null), i(ModItems.LAVA_OBSIDIAN_AXE.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.tool.shovel", shaped(m, null, null, Items.STICK, null, null, Items.STICK, null, null), i(ModItems.LAVA_OBSIDIAN_SHOVEL.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.tool.hoe", shaped(m, m, null, null, Items.STICK, null, null, Items.STICK, null), i(ModItems.LAVA_OBSIDIAN_HOE.get())));
        return list;
    }

    private static List<Page> armor() {
        ItemLike m = ModBlocks.NETHERBRICKLAVAOBSIDIAN.get();
        List<Page> list = new ArrayList<>();
        list.add(Page.crafting("gui.worlds_with_ores.book.armor.helmet", shaped(m, m, m, m, null, m, null, null, null), i(ModItems.LAVA_OBSIDIAN_HELMET.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.armor.chestplate", shaped(m, null, m, m, m, m, m, m, m), i(ModItems.LAVA_OBSIDIAN_CHESTPLATE.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.armor.leggings", shaped(m, m, m, m, null, m, m, null, m), i(ModItems.LAVA_OBSIDIAN_LEGGINGS.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.armor.boots", shaped(null, null, null, m, null, m, m, null, m), i(ModItems.LAVA_OBSIDIAN_BOOTS.get())));
        return list;
    }

    private static List<Page> blocks() {
        List<Page> list = new ArrayList<>();
        list.add(Page.crafting("gui.worlds_with_ores.book.block.chest", shaped(
                Items.RED_WOOL, Items.BLACK_WOOL, Items.BLUE_WOOL,
                Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS,
                Items.IRON_BLOCK, null, Items.IRON_BLOCK
        ), i(ModItems.CHEST.get())));
        list.add(Page.crafting("gui.worlds_with_ores.book.block.book", shaped(
                Items.OAK_SAPLING, Items.BOOK, null,
                null, null, null,
                null, null, null
        ), i(ModItems.RECIPES_BOOK.get())));
        return list;
    }
}
