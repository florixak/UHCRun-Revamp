package me.florixak.uhcrevamp.game.customRecipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public interface RecipeUtils {

    ShapedRecipe createRecipe(ItemStack item, String key);
}
