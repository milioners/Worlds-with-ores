package net.millioners.worldswithores.registry;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.millioners.worldswithores.recipe.FluxCoilUpgradeRecipe;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WorldsWithOresMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<FluxCoilUpgradeRecipe>> FLUX_COIL_UPGRADE =
            SERIALIZERS.register("flux_coil_upgrade",
                    () -> new SimpleCraftingRecipeSerializer<>(FluxCoilUpgradeRecipe::new));

    private ModRecipes() {}
}
