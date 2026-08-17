package net.millioners.worldswithores.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModItems;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

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
        INFO
    }

    public record Page(
            Component title,
            Kind kind,
            ItemStack[] pattern,
            ItemStack result,
            Component hint
    ) {
        public static Page crafting(String title, ItemStack[] pattern, ItemStack result) {
            return new Page(Component.literal(title), Kind.CRAFTING, pattern, result, Component.empty());
        }

        public static Page crafting(String title, ItemStack[] pattern, ItemStack result, String hint) {
            return new Page(Component.literal(title), Kind.CRAFTING, pattern, result, Component.literal(hint));
        }

        public static Page smelting(String title, ItemStack input, ItemStack output, String hint) {
            return new Page(Component.literal(title), Kind.SMELTING, new ItemStack[]{input}, output, Component.literal(hint));
        }

        public static Page info(String title, ItemStack[] icons, String hint) {
            return new Page(Component.literal(title), Kind.INFO, icons, ItemStack.EMPTY, Component.literal(hint));
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
        list.add(Page.info("How Portals Work", icons(
                Items.COAL_BLOCK, ModItems.INGOT_COAL.get(), ModItems.COALWORD.get()
        ), "1) Build a frame from resource blocks\n2) Craft the matching igniter\n3) Right-click inside the frame"));
        list.add(Page.info("Progression", icons(
                ModItems.CATALYST_ORE_COAL.get(),
                ModItems.CATALYST_ORE_IRON.get(),
                ModItems.CATALYST_ORE_GOLD.get(),
                ModItems.CATALYST_ORE_DIAMOND.get()
        ), "Overworld coal ore -> Coalworld\nCoalworld iron ore -> Ironworld\nThen gold, diamond, emerald,\nlapis, redstone worlds."));
        list.add(Page.info("Smelting Tip", icons(
                ModItems.CATALYST_COAL.get(), Items.FURNACE, ModItems.INGOT_COAL.get()
        ), "Mine portal ores to get raw catalysts.\nSmelt raw (or ore) into portal ingots.\nUse 8 ingots + flint for igniters."));
        return list;
    }

    private static List<Page> materials() {
        // One page per material (no ore/raw duplicate)
        List<Page> list = new ArrayList<>();
        list.add(Page.smelting("Coal Catalyst", i(ModItems.CATALYST_COAL.get()), i(ModItems.INGOT_COAL.get()),
                "Also works with Coal Portal Ore"));
        list.add(Page.smelting("Iron Catalyst", i(ModItems.CATALYST_IRON.get()), i(ModItems.INGOT_IRON.get()),
                "Also works with Iron Portal Ore"));
        list.add(Page.smelting("Gold Catalyst", i(ModItems.CATALYST_GOLD.get()), i(ModItems.INGOT_GOLD.get()),
                "Also works with Gold Portal Ore"));
        list.add(Page.smelting("Diamond Catalyst", i(ModItems.CATALYST_DIAMOND.get()), i(ModItems.INGOT_DIAMOND.get()),
                "Also works with Diamond Portal Ore"));
        list.add(Page.smelting("Emerald Catalyst", i(ModItems.CATALYST_EMERALD.get()), i(ModItems.INGOT_EMERALD.get()),
                "Also works with Emerald Portal Ore"));
        list.add(Page.smelting("Lapis Catalyst", i(ModItems.CATALYST_LAPIS.get()), i(ModItems.INGOT_LAPIS.get()),
                "Also works with Lapis Portal Ore"));
        list.add(Page.smelting("Redstone Catalyst", i(ModItems.CATALYST_REDSTONE.get()), i(ModItems.INGOT_REDSTONE.get()),
                "Also works with Redstone Portal Ore"));
        return list;
    }

    private static List<Page> portals() {
        List<Page> list = new ArrayList<>();
        list.add(frameAndIgniter("Coalworld", Blocks.COAL_BLOCK, ModItems.INGOT_COAL.get(), ModItems.COALWORD.get()));
        list.add(igniter("Coalworld Igniter", ModItems.INGOT_COAL.get(), ModItems.COALWORD.get()));
        list.add(frameAndIgniter("Ironworld", Blocks.IRON_BLOCK, ModItems.INGOT_IRON.get(), ModItems.IRONWORLD.get()));
        list.add(igniter("Ironworld Igniter", ModItems.INGOT_IRON.get(), ModItems.IRONWORLD.get()));
        list.add(frameAndIgniter("Goldworld", Blocks.GOLD_BLOCK, ModItems.INGOT_GOLD.get(), ModItems.GOLDWORLD.get()));
        list.add(igniter("Goldworld Igniter", ModItems.INGOT_GOLD.get(), ModItems.GOLDWORLD.get()));
        list.add(frameAndIgniter("Diamondworld", Blocks.DIAMOND_BLOCK, ModItems.INGOT_DIAMOND.get(), ModItems.DIAMONDWORLD.get()));
        list.add(igniter("Diamondworld Igniter", ModItems.INGOT_DIAMOND.get(), ModItems.DIAMONDWORLD.get()));
        list.add(frameAndIgniter("Emeraldworld", Blocks.EMERALD_BLOCK, ModItems.INGOT_EMERALD.get(), ModItems.EMERALDWORLD.get()));
        list.add(igniter("Emeraldworld Igniter", ModItems.INGOT_EMERALD.get(), ModItems.EMERALDWORLD.get()));
        list.add(frameAndIgniter("Lapisworld", Blocks.LAPIS_BLOCK, ModItems.INGOT_LAPIS.get(), ModItems.LAPISWORLD.get()));
        list.add(igniter("Lapisworld Igniter", ModItems.INGOT_LAPIS.get(), ModItems.LAPISWORLD.get()));
        list.add(frameAndIgniter("Redstoneworld", Blocks.REDSTONE_BLOCK, ModItems.INGOT_REDSTONE.get(), ModItems.REDSTONEWORLD.get()));
        list.add(igniter("Redstoneworld Igniter", ModItems.INGOT_REDSTONE.get(), ModItems.REDSTONEWORLD.get()));
        return list;
    }

    /** Visual frame layout (10 blocks ring) — not a craft result, shows what to build. */
    private static Page frameAndIgniter(String world, ItemLike frame, ItemLike ingot, ItemLike igniter) {
        return Page.crafting(world + " Frame", shaped(
                frame, frame, frame,
                frame, null, frame,
                frame, frame, frame
        ), i(igniter), "Build this frame (2x3 hollow),\nthen use the " + world + " igniter.");
    }

    private static Page igniter(String name, ItemLike ore, ItemLike result) {
        return Page.crafting(name, shaped(
                ore, ore, ore,
                ore, Items.FLINT, ore,
                ore, ore, ore
        ), i(result), "Right-click the matching frame.");
    }

    private static List<Page> compat() {
        List<Page> list = new ArrayList<>();
        list.add(Page.info("Industrial Soft Support", icons(
                Items.IRON_PICKAXE, Items.FURNACE, Items.BOOK
        ), "If Create / Mekanism / IE / Thermal /\nExtreme Reactors / AE2 are installed,\ntheir ores also generate in Overworld\nand ore dimensions. No extra download."));

        addCompatOrePage(list, "create", "Create Zinc", "create:zinc_ore", "create:raw_zinc");
        addCompatOrePage(list, "mekanism", "Mekanism Osmium", "mekanism:osmium_ore", "mekanism:raw_osmium");
        addCompatOrePage(list, "mekanism", "Mekanism Tin", "mekanism:tin_ore", "mekanism:raw_tin");
        addCompatOrePage(list, "immersiveengineering", "IE Aluminum", "immersiveengineering:ore_aluminum", "immersiveengineering:raw_aluminum");
        addCompatOrePage(list, "thermal", "Thermal Tin", "thermal:tin_ore", "thermal:tin_ingot");
        addCompatOrePage(list, "bigreactors", "Yellorite", "bigreactors:yellorite_ore", "bigreactors:yellorium_ingot");
        addCompatOrePage(list, "ae2", "AE2 Quartz", "ae2:quartz_cluster", "ae2:certus_quartz_crystal");

        list.add(Page.info("Machine Processing", icons(
                ModItems.CATALYST_ORE_IRON.get(), ModItems.CATALYST_IRON.get(), ModItems.INGOT_IRON.get()
        ), "Portal ores/raw work in:\nCreate crushing, Mekanism enriching,\nThermal pulverizer/smelter,\nIE crusher — when those mods exist."));
        return list;
    }

    private static void addCompatOrePage(List<Page> list, String modId, String title, String oreId, String productId) {
        if (!ModList.get().isLoaded(modId)) {
            return;
        }
        ItemStack ore = optionalItem(oreId);
        ItemStack product = optionalItem(productId);
        if (ore.isEmpty()) {
            return;
        }
        list.add(Page.info(title + " (installed)", new ItemStack[]{ore, product.isEmpty() ? ItemStack.EMPTY : product},
                "Generates in Overworld + ore worlds.\nMine it like a normal " + modId + " ore."));
    }

    private static List<Page> tools() {
        ItemLike m = ModBlocks.NETHERBRICKLAVAOBSIDIAN.get();
        List<Page> list = new ArrayList<>();
        list.add(Page.crafting("Lava Obsidian Sword", shaped(m, null, null, m, null, null, Items.STICK, null, null), i(ModItems.LAVA_OBSIDIAN_SWORD.get())));
        list.add(Page.crafting("Lava Obsidian Pickaxe", shaped(m, m, m, null, Items.STICK, null, null, Items.STICK, null), i(ModItems.LAVA_OBSIDIAN_PICKAXE.get())));
        list.add(Page.crafting("Lava Obsidian Axe", shaped(m, m, null, m, Items.STICK, null, null, Items.STICK, null), i(ModItems.LAVA_OBSIDIAN_AXE.get())));
        list.add(Page.crafting("Lava Obsidian Shovel", shaped(m, null, null, Items.STICK, null, null, Items.STICK, null, null), i(ModItems.LAVA_OBSIDIAN_SHOVEL.get())));
        list.add(Page.crafting("Lava Obsidian Hoe", shaped(m, m, null, null, Items.STICK, null, null, Items.STICK, null), i(ModItems.LAVA_OBSIDIAN_HOE.get())));
        return list;
    }

    private static List<Page> armor() {
        ItemLike m = ModBlocks.NETHERBRICKLAVAOBSIDIAN.get();
        List<Page> list = new ArrayList<>();
        list.add(Page.crafting("Helmet", shaped(m, m, m, m, null, m, null, null, null), i(ModItems.LAVA_OBSIDIAN_HELMET.get())));
        list.add(Page.crafting("Chestplate", shaped(m, null, m, m, m, m, m, m, m), i(ModItems.LAVA_OBSIDIAN_CHESTPLATE.get())));
        list.add(Page.crafting("Leggings", shaped(m, m, m, m, null, m, m, null, m), i(ModItems.LAVA_OBSIDIAN_LEGGINGS.get())));
        list.add(Page.crafting("Boots", shaped(null, null, null, m, null, m, m, null, m), i(ModItems.LAVA_OBSIDIAN_BOOTS.get())));
        return list;
    }

    private static List<Page> blocks() {
        List<Page> list = new ArrayList<>();
        list.add(Page.crafting("Mod Chest", shaped(
                Items.RED_WOOL, Items.BLACK_WOOL, Items.BLUE_WOOL,
                Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS,
                Items.IRON_BLOCK, null, Items.IRON_BLOCK
        ), i(ModItems.CHEST.get())));
        list.add(Page.crafting("Recipes Book", shaped(
                Items.OAK_SAPLING, Items.BOOK, null,
                null, null, null,
                null, null, null
        ), i(ModItems.RECIPES_BOOK.get())));
        return list;
    }
}
