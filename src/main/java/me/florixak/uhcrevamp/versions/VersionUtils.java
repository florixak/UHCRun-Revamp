package me.florixak.uhcrevamp.versions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Set;

public interface VersionUtils {

    Set<Material> getWoodPlankValues();

    ItemStack getLapis();

    ShapedRecipe createRecipe(ItemStack item, String key);

}
