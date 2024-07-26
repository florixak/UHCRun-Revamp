package me.florixak.uhcrevamp.versions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.EnumSet;
import java.util.Set;

public class VersionUtils_1_8 implements VersionUtils {

    @Override
    public Set<Material> getWoodValues() {
        return EnumSet.of(Material.valueOf("WOOD"));
    }

    @Override
    public ShapedRecipe createRecipe(ItemStack item, String key) {
        return new ShapedRecipe(item);
    }


}
