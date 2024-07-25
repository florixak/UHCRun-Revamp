package me.florixak.uhcrevamp.game.customRecipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

@SuppressWarnings("deprecation")
public class RecipeUtils_1_8 implements RecipeUtils {

    @Override
    public ShapedRecipe createRecipe(ItemStack item, String key) {
        return new ShapedRecipe(item);
    }

}
