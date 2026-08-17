package net.millioners.worldswithores.registry;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.world.ore.OptionalOreFeature;
import net.millioners.worldswithores.world.ore.SoftTerrainFillFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, WorldsWithOresMod.MOD_ID);

    public static final RegistryObject<Feature<OptionalOreFeature.Config>> OPTIONAL_ORE =
            FEATURES.register("optional_ore", OptionalOreFeature::new);

    public static final RegistryObject<Feature<SoftTerrainFillFeature.Config>> SOFT_TERRAIN_FILL =
            FEATURES.register("soft_terrain_fill", SoftTerrainFillFeature::new);
}
