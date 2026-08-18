package net.millioners.worldswithores.registry;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Blocks;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.item.ModArmorMaterials;
import net.millioners.worldswithores.item.ModTiers;
import net.millioners.worldswithores.item.PortalFluxPickaxeItem;
import net.millioners.worldswithores.item.PortalIgniterItem;
import net.millioners.worldswithores.item.RecipesBookItem;
import net.millioners.worldswithores.util.SoftFrames;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WorldsWithOresMod.MOD_ID);

    private static RegistryObject<Item> blockItem(String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final RegistryObject<Item> NETHERBRICKBRINSTAR = blockItem("netherbrickbrinstar", ModBlocks.NETHERBRICKBRINSTAR);
    public static final RegistryObject<Item> NETHERBRICKCLASSICSPATTER = blockItem("netherbrickclassicspatter", ModBlocks.NETHERBRICKCLASSICSPATTER);
    public static final RegistryObject<Item> NETHERBRICKGUTS = blockItem("netherbrickguts", ModBlocks.NETHERBRICKGUTS);
    public static final RegistryObject<Item> NETHERBRICKLAVABROWN = blockItem("netherbricklavabrown", ModBlocks.NETHERBRICKLAVABROWN);
    public static final RegistryObject<Item> NETHERBRICKLAVAOBSIDIAN = blockItem("netherbricklavaobsidian", ModBlocks.NETHERBRICKLAVAOBSIDIAN);
    public static final RegistryObject<Item> NETHERBRICKLAVASTONEDARK = blockItem("netherbricklavastonedark", ModBlocks.NETHERBRICKLAVASTONEDARK);
    public static final RegistryObject<Item> CHEST = blockItem("chest", ModBlocks.CHEST);

    public static final RegistryObject<Item> CATALYST_ORE_COAL = blockItem("catalyst_ore_coal", ModBlocks.CATALYST_ORE_COAL);
    public static final RegistryObject<Item> CATALYST_ORE_IRON = blockItem("catalyst_ore_iron", ModBlocks.CATALYST_ORE_IRON);
    public static final RegistryObject<Item> CATALYST_ORE_GOLD = blockItem("catalyst_ore_gold", ModBlocks.CATALYST_ORE_GOLD);
    public static final RegistryObject<Item> CATALYST_ORE_DIAMOND = blockItem("catalyst_ore_diamond", ModBlocks.CATALYST_ORE_DIAMOND);
    public static final RegistryObject<Item> CATALYST_ORE_EMERALD = blockItem("catalyst_ore_emerald", ModBlocks.CATALYST_ORE_EMERALD);
    public static final RegistryObject<Item> CATALYST_ORE_LAPIS = blockItem("catalyst_ore_lapis", ModBlocks.CATALYST_ORE_LAPIS);
    public static final RegistryObject<Item> CATALYST_ORE_REDSTONE = blockItem("catalyst_ore_redstone", ModBlocks.CATALYST_ORE_REDSTONE);

    public static final RegistryObject<Item> CATALYST_COAL = ITEMS.register("catalyst_coal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CATALYST_IRON = ITEMS.register("catalyst_iron", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CATALYST_GOLD = ITEMS.register("catalyst_gold", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CATALYST_DIAMOND = ITEMS.register("catalyst_diamond", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CATALYST_EMERALD = ITEMS.register("catalyst_emerald", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CATALYST_LAPIS = ITEMS.register("catalyst_lapis", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CATALYST_REDSTONE = ITEMS.register("catalyst_redstone", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> INGOT_COAL = ITEMS.register("ingot_coal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_IRON = ITEMS.register("ingot_iron", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_GOLD = ITEMS.register("ingot_gold", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_DIAMOND = ITEMS.register("ingot_diamond", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_EMERALD = ITEMS.register("ingot_emerald", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_LAPIS = ITEMS.register("ingot_lapis", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_REDSTONE = ITEMS.register("ingot_redstone", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COALWORD = ITEMS.register("coalword",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64), () -> Blocks.COAL_BLOCK, ModBlocks.COALWORD_PORTAL));
    public static final RegistryObject<Item> IRONWORLD = ITEMS.register("ironworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64), () -> Blocks.IRON_BLOCK, ModBlocks.IRONWORLD_PORTAL));
    public static final RegistryObject<Item> GOLDWORLD = ITEMS.register("goldworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64), () -> Blocks.GOLD_BLOCK, ModBlocks.GOLDWORLD_PORTAL));
    public static final RegistryObject<Item> DIAMONDWORLD = ITEMS.register("diamondworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64), () -> Blocks.DIAMOND_BLOCK, ModBlocks.DIAMONDWORLD_PORTAL));
    public static final RegistryObject<Item> EMERALDWORLD = ITEMS.register("emeraldworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64), () -> Blocks.EMERALD_BLOCK, ModBlocks.EMERALDWORLD_PORTAL));
    public static final RegistryObject<Item> LAPISWORLD = ITEMS.register("lapisworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64), () -> Blocks.LAPIS_BLOCK, ModBlocks.LAPISWORLD_PORTAL));
    public static final RegistryObject<Item> REDSTONEWORLD = ITEMS.register("redstoneworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64), () -> Blocks.REDSTONE_BLOCK, ModBlocks.REDSTONEWORLD_PORTAL));

    public static final RegistryObject<Item> ZINCWORLD = ITEMS.register("zincworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64),
                    SoftFrames.only("create:zinc_block"), ModBlocks.ZINCWORLD_PORTAL));
    public static final RegistryObject<Item> OSMIUMWORLD = ITEMS.register("osmiumworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64),
                    SoftFrames.only("mekanism:block_osmium"), ModBlocks.OSMIUMWORLD_PORTAL));
    public static final RegistryObject<Item> ALUMINUMWORLD = ITEMS.register("aluminumworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64),
                    SoftFrames.only("immersiveengineering:storage_aluminum"), ModBlocks.ALUMINUMWORLD_PORTAL));
    public static final RegistryObject<Item> SILVERWORLD = ITEMS.register("silverworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64),
                    SoftFrames.only("thermal:silver_block"), ModBlocks.SILVERWORLD_PORTAL));
    public static final RegistryObject<Item> YELLORIUMWORLD = ITEMS.register("yelloriumworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64),
                    SoftFrames.only("bigreactors:yellorium_block"), ModBlocks.YELLORIUMWORLD_PORTAL));
    public static final RegistryObject<Item> CERTUSWORLD = ITEMS.register("certusworld",
            () -> new PortalIgniterItem(new Item.Properties().stacksTo(64),
                    SoftFrames.only("ae2:quartz_block"), ModBlocks.CERTUSWORLD_PORTAL));

    public static final RegistryObject<Item> FLUX_CASING = blockItem("flux_casing", ModBlocks.FLUX_CASING);
    public static final RegistryObject<Item> FLUX_COIL = blockItem("flux_coil", ModBlocks.FLUX_COIL);
    public static final RegistryObject<Item> FLUX_CONTROLLER = blockItem("flux_controller", ModBlocks.FLUX_CONTROLLER);
    public static final RegistryObject<Item> FLUX_BATTERY = blockItem("flux_battery", ModBlocks.FLUX_BATTERY);
    public static final RegistryObject<Item> FLUX_CHARGER = blockItem("flux_charger", ModBlocks.FLUX_CHARGER);

    public static final RegistryObject<Item> FLUX_CORE = ITEMS.register("flux_core",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> PORTAL_FLUX_PICKAXE = ITEMS.register("portal_flux_pickaxe",
            PortalFluxPickaxeItem::new);

    // Display-only 3D portal models for the recipe book
    public static final RegistryObject<Item> PORTAL_PREVIEW_COAL = ITEMS.register("portal_preview_coal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_IRON = ITEMS.register("portal_preview_iron", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_GOLD = ITEMS.register("portal_preview_gold", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_DIAMOND = ITEMS.register("portal_preview_diamond", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_EMERALD = ITEMS.register("portal_preview_emerald", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_LAPIS = ITEMS.register("portal_preview_lapis", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_REDSTONE = ITEMS.register("portal_preview_redstone", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_ZINC = ITEMS.register("portal_preview_zinc", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_OSMIUM = ITEMS.register("portal_preview_osmium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_ALUMINUM = ITEMS.register("portal_preview_aluminum", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_SILVER = ITEMS.register("portal_preview_silver", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_YELLORIUM = ITEMS.register("portal_preview_yellorium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_PREVIEW_CERTUS = ITEMS.register("portal_preview_certus", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LAVA_OBSIDIAN_SWORD = ITEMS.register("lava_obsidian_sword",
            () -> new SwordItem(ModTiers.LAVA_OBSIDIAN, 3, -2.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN_PICKAXE = ITEMS.register("lava_obsidian_pickaxe",
            () -> new PickaxeItem(ModTiers.LAVA_OBSIDIAN, 1, -2.8F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN_AXE = ITEMS.register("lava_obsidian_axe",
            () -> new AxeItem(ModTiers.LAVA_OBSIDIAN, 5.0F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN_SHOVEL = ITEMS.register("lava_obsidian_shovel",
            () -> new ShovelItem(ModTiers.LAVA_OBSIDIAN, 1.5F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN_HOE = ITEMS.register("lava_obsidian_hoe",
            () -> new HoeItem(ModTiers.LAVA_OBSIDIAN, -4, 0.0F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> LAVA_OBSIDIAN_HELMET = ITEMS.register("lava_obsidian_armor_helmet",
            () -> new ArmorItem(ModArmorMaterials.LAVA_OBSIDIAN, ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN_CHESTPLATE = ITEMS.register("lava_obsidian_armor_chestplate",
            () -> new ArmorItem(ModArmorMaterials.LAVA_OBSIDIAN, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN_LEGGINGS = ITEMS.register("lava_obsidian_armor_leggings",
            () -> new ArmorItem(ModArmorMaterials.LAVA_OBSIDIAN, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN_BOOTS = ITEMS.register("lava_obsidian_armor_boots",
            () -> new ArmorItem(ModArmorMaterials.LAVA_OBSIDIAN, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> RECIPES_BOOK = ITEMS.register("recipes_book",
            () -> new RecipesBookItem(new Item.Properties().stacksTo(1)));
}
