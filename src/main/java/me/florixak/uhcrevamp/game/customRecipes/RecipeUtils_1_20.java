package me.florixak.uhcrevamp.game.customRecipes;

import me.florixak.uhcrevamp.UHCRevamp;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeUtils_1_20 implements RecipeUtils {

    @Override
    public ShapedRecipe createRecipe(ItemStack item, String key) {
        return new ShapedRecipe(new NamespacedKey(UHCRevamp.getInstance(), key), item);
    }
}
