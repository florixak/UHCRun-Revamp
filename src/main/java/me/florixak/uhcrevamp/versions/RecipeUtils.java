package me.florixak.uhcrevamp.versions;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public interface RecipeUtils {

    ShapedRecipe createRecipe(ItemStack item, String key);
}
