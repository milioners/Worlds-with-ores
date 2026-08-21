package net.millioners.worldswithores.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.millioners.worldswithores.block.FluxCoilTier;
import net.millioners.worldswithores.item.FluxCoilBlockItem;
import net.millioners.worldswithores.item.FluxCoilUpgradeItem;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModItems;
import net.millioners.worldswithores.registry.ModRecipes;

/**
 * Shapeless: Flux Coil + matching upgrade -> higher-tier coil item.
 */
public class FluxCoilUpgradeRecipe extends CustomRecipe {
    public FluxCoilUpgradeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return createResult(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack result = createResult(container);
        return result == null ? ItemStack.EMPTY : result;
    }

    private ItemStack createResult(CraftingContainer container) {
        ItemStack coil = ItemStack.EMPTY;
        ItemStack upgrade = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(ModItems.FLUX_COIL.get())) {
                if (!coil.isEmpty()) {
                    return null;
                }
                coil = stack;
            } else if (stack.getItem() instanceof FluxCoilUpgradeItem) {
                if (!upgrade.isEmpty()) {
                    return null;
                }
                upgrade = stack;
            } else {
                return null;
            }
        }
        if (coil.isEmpty() || upgrade.isEmpty()) {
            return null;
        }
        FluxCoilTier current = FluxCoilBlockItem.getTier(coil);
        FluxCoilTier required = ((FluxCoilUpgradeItem) upgrade.getItem()).fromTier();
        if (current != required) {
            return null;
        }
        return FluxCoilBlockItem.withTier(new ItemStack(ModBlocks.FLUX_COIL.get()), required.next());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FLUX_COIL_UPGRADE.get();
    }
}
