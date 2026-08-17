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
import net.millioners.worldswithores.block.ModChestBlock;
import net.millioners.worldswithores.block.ModPortalBlock;
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
}
