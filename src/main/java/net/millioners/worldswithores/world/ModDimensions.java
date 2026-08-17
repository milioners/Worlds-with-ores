package net.millioners.worldswithores.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.millioners.worldswithores.WorldsWithOresMod;

public final class ModDimensions {
    private ModDimensions() {}

    public static final ResourceKey<Level> COALWORD = level("coalword");
    public static final ResourceKey<Level> IRONWORLD = level("ironworld");
    public static final ResourceKey<Level> GOLDWORLD = level("goldworld");
    public static final ResourceKey<Level> DIAMONDWORLD = level("diamondworld");
    public static final ResourceKey<Level> EMERALDWORLD = level("emeraldworld");
    public static final ResourceKey<Level> LAPISWORLD = level("lapisworld");
    public static final ResourceKey<Level> REDSTONEWORLD = level("redstoneworld");

    public static final ResourceKey<Level> ZINCWORLD = level("zincworld");
    public static final ResourceKey<Level> OSMIUMWORLD = level("osmiumworld");
    public static final ResourceKey<Level> ALUMINUMWORLD = level("aluminumworld");
    public static final ResourceKey<Level> SILVERWORLD = level("silverworld");
    public static final ResourceKey<Level> YELLORIUMWORLD = level("yelloriumworld");
    public static final ResourceKey<Level> CERTUSWORLD = level("certusworld");

    public static final ResourceKey<DimensionType> COALWORD_TYPE = dimType("coalword");
    public static final ResourceKey<DimensionType> IRONWORLD_TYPE = dimType("ironworld");
    public static final ResourceKey<DimensionType> GOLDWORLD_TYPE = dimType("goldworld");
    public static final ResourceKey<DimensionType> DIAMONDWORLD_TYPE = dimType("diamondworld");
    public static final ResourceKey<DimensionType> EMERALDWORLD_TYPE = dimType("emeraldworld");
    public static final ResourceKey<DimensionType> LAPISWORLD_TYPE = dimType("lapisworld");
    public static final ResourceKey<DimensionType> REDSTONEWORLD_TYPE = dimType("redstoneworld");

    public static final ResourceKey<DimensionType> ZINCWORLD_TYPE = dimType("zincworld");
    public static final ResourceKey<DimensionType> OSMIUMWORLD_TYPE = dimType("osmiumworld");
    public static final ResourceKey<DimensionType> ALUMINUMWORLD_TYPE = dimType("aluminumworld");
    public static final ResourceKey<DimensionType> SILVERWORLD_TYPE = dimType("silverworld");
    public static final ResourceKey<DimensionType> YELLORIUMWORLD_TYPE = dimType("yelloriumworld");
    public static final ResourceKey<DimensionType> CERTUSWORLD_TYPE = dimType("certusworld");

    private static ResourceKey<Level> level(String name) {
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldsWithOresMod.MOD_ID, name));
    }

    private static ResourceKey<DimensionType> dimType(String name) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(WorldsWithOresMod.MOD_ID, name));
    }
}
