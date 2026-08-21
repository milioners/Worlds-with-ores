package net.millioners.worldswithores.registry;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.block.FluxBatteryBlock;
import net.millioners.worldswithores.block.FluxCableBlock;
import net.millioners.worldswithores.block.FluxCasingBlock;
import net.millioners.worldswithores.block.FluxChargerBlock;
import net.millioners.worldswithores.block.FluxCoilBlock;
import net.millioners.worldswithores.block.FluxControllerBlock;
import net.millioners.worldswithores.block.FluxEnergyPortBlock;
import net.millioners.worldswithores.block.FluxGlassBlock;
import net.millioners.worldswithores.block.FluxWirelessBlock;
import net.millioners.worldswithores.block.ModChestBlock;
import net.millioners.worldswithores.block.ModPortalBlock;
import net.millioners.worldswithores.util.SoftFrames;
import net.millioners.worldswithores.world.ModDimensions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WorldsWithOresMod.MOD_ID);

    private static BlockBehaviour.Properties stoneLike() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(2.0F, 10.0F)
                .sound(SoundType.STONE);
    }

    private static BlockBehaviour.Properties oreProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 3.0F)
                .sound(SoundType.STONE);
    }

    public static final RegistryObject<Block> NETHERBRICKBRINSTAR =
            BLOCKS.register("netherbrickbrinstar", () -> new Block(stoneLike()));
    public static final RegistryObject<Block> NETHERBRICKCLASSICSPATTER =
            BLOCKS.register("netherbrickclassicspatter", () -> new Block(stoneLike()));
    public static final RegistryObject<Block> NETHERBRICKGUTS =
            BLOCKS.register("netherbrickguts", () -> new Block(stoneLike()));
    public static final RegistryObject<Block> NETHERBRICKLAVABROWN =
            BLOCKS.register("netherbricklavabrown", () -> new Block(stoneLike()));
    public static final RegistryObject<Block> NETHERBRICKLAVAOBSIDIAN =
            BLOCKS.register("netherbricklavaobsidian", () -> new Block(stoneLike()));
    public static final RegistryObject<Block> NETHERBRICKLAVASTONEDARK =
            BLOCKS.register("netherbricklavastonedark", () -> new Block(stoneLike()));

    // Progressive portal catalyst ores
    public static final RegistryObject<Block> CATALYST_ORE_COAL =
            BLOCKS.register("catalyst_ore_coal", () -> new DropExperienceBlock(oreProps(), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> CATALYST_ORE_IRON =
            BLOCKS.register("catalyst_ore_iron", () -> new DropExperienceBlock(oreProps(), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> CATALYST_ORE_GOLD =
            BLOCKS.register("catalyst_ore_gold", () -> new DropExperienceBlock(oreProps(), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> CATALYST_ORE_DIAMOND =
            BLOCKS.register("catalyst_ore_diamond", () -> new DropExperienceBlock(oreProps(), UniformInt.of(2, 5)));
    public static final RegistryObject<Block> CATALYST_ORE_EMERALD =
            BLOCKS.register("catalyst_ore_emerald", () -> new DropExperienceBlock(oreProps(), UniformInt.of(2, 5)));
    public static final RegistryObject<Block> CATALYST_ORE_LAPIS =
            BLOCKS.register("catalyst_ore_lapis", () -> new DropExperienceBlock(oreProps(), UniformInt.of(1, 4)));
    public static final RegistryObject<Block> CATALYST_ORE_REDSTONE =
            BLOCKS.register("catalyst_ore_redstone", () -> new DropExperienceBlock(oreProps(), UniformInt.of(1, 4)));

    public static final RegistryObject<ModChestBlock> CHEST =
            BLOCKS.register("chest", ModChestBlock::new);

    public static final RegistryObject<ModPortalBlock> COALWORD_PORTAL = BLOCKS.register("coalword_portal",
            () -> new ModPortalBlock(() -> Blocks.COAL_BLOCK, ModDimensions.COALWORD));
    public static final RegistryObject<ModPortalBlock> IRONWORLD_PORTAL = BLOCKS.register("ironworld_portal",
            () -> new ModPortalBlock(() -> Blocks.IRON_BLOCK, ModDimensions.IRONWORLD));
    public static final RegistryObject<ModPortalBlock> GOLDWORLD_PORTAL = BLOCKS.register("goldworld_portal",
            () -> new ModPortalBlock(() -> Blocks.GOLD_BLOCK, ModDimensions.GOLDWORLD));
    public static final RegistryObject<ModPortalBlock> DIAMONDWORLD_PORTAL = BLOCKS.register("diamondworld_portal",
            () -> new ModPortalBlock(() -> Blocks.DIAMOND_BLOCK, ModDimensions.DIAMONDWORLD));
    public static final RegistryObject<ModPortalBlock> EMERALDWORLD_PORTAL = BLOCKS.register("emeraldworld_portal",
            () -> new ModPortalBlock(() -> Blocks.EMERALD_BLOCK, ModDimensions.EMERALDWORLD));
    public static final RegistryObject<ModPortalBlock> LAPISWORLD_PORTAL = BLOCKS.register("lapisworld_portal",
            () -> new ModPortalBlock(() -> Blocks.LAPIS_BLOCK, ModDimensions.LAPISWORLD));
    public static final RegistryObject<ModPortalBlock> REDSTONEWORLD_PORTAL = BLOCKS.register("redstoneworld_portal",
            () -> new ModPortalBlock(() -> Blocks.REDSTONE_BLOCK, ModDimensions.REDSTONEWORLD));

    public static final RegistryObject<ModPortalBlock> ZINCWORLD_PORTAL = BLOCKS.register("zincworld_portal",
            () -> new ModPortalBlock(SoftFrames.only("create:zinc_block"), ModDimensions.ZINCWORLD));
    public static final RegistryObject<ModPortalBlock> OSMIUMWORLD_PORTAL = BLOCKS.register("osmiumworld_portal",
            () -> new ModPortalBlock(SoftFrames.only("mekanism:block_osmium"), ModDimensions.OSMIUMWORLD));
    public static final RegistryObject<ModPortalBlock> ALUMINUMWORLD_PORTAL = BLOCKS.register("aluminumworld_portal",
            () -> new ModPortalBlock(SoftFrames.only("immersiveengineering:storage_aluminum"), ModDimensions.ALUMINUMWORLD));
    public static final RegistryObject<ModPortalBlock> SILVERWORLD_PORTAL = BLOCKS.register("silverworld_portal",
            () -> new ModPortalBlock(SoftFrames.only("thermal:silver_block"), ModDimensions.SILVERWORLD));
    public static final RegistryObject<ModPortalBlock> YELLORIUMWORLD_PORTAL = BLOCKS.register("yelloriumworld_portal",
            () -> new ModPortalBlock(SoftFrames.only("bigreactors:yellorium_block"), ModDimensions.YELLORIUMWORLD));
    public static final RegistryObject<ModPortalBlock> CERTUSWORLD_PORTAL = BLOCKS.register("certusworld_portal",
            () -> new ModPortalBlock(SoftFrames.only("ae2:quartz_block"), ModDimensions.CERTUSWORLD));

    public static final RegistryObject<Block> FLUX_CASING = BLOCKS.register("flux_casing", FluxCasingBlock::new);
    public static final RegistryObject<Block> FLUX_COIL = BLOCKS.register("flux_coil", FluxCoilBlock::new);
    public static final RegistryObject<Block> FLUX_GLASS = BLOCKS.register("flux_glass", FluxGlassBlock::new);
    public static final RegistryObject<FluxEnergyPortBlock> FLUX_ENERGY_PORT =
            BLOCKS.register("flux_energy_port", FluxEnergyPortBlock::new);
    public static final RegistryObject<FluxControllerBlock> FLUX_CONTROLLER =
            BLOCKS.register("flux_controller", FluxControllerBlock::new);
    public static final RegistryObject<FluxBatteryBlock> FLUX_BATTERY =
            BLOCKS.register("flux_battery", FluxBatteryBlock::new);
    public static final RegistryObject<FluxChargerBlock> FLUX_CHARGER =
            BLOCKS.register("flux_charger", FluxChargerBlock::new);
    public static final RegistryObject<FluxCableBlock> FLUX_CABLE =
            BLOCKS.register("flux_cable", FluxCableBlock::new);
    public static final RegistryObject<FluxWirelessBlock> FLUX_WIRELESS_TRANSMITTER =
            BLOCKS.register("flux_wireless_transmitter", () -> new FluxWirelessBlock(true));
    public static final RegistryObject<FluxWirelessBlock> FLUX_WIRELESS_RECEIVER =
            BLOCKS.register("flux_wireless_receiver", () -> new FluxWirelessBlock(false));
}
