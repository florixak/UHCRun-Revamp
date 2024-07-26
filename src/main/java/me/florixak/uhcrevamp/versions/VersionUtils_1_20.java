package me.florixak.uhcrevamp.versions;

import me.florixak.uhcrevamp.UHCRevamp;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.EnumSet;
import java.util.Set;

public class VersionUtils_1_20 implements VersionUtils {

    @Override
    public Set<Material> getWoodPlankValues() {
        return EnumSet.of(Material.OAK_PLANKS,
                Material.SPRUCE_PLANKS,
                Material.BIRCH_PLANKS,
                Material.JUNGLE_PLANKS,
                Material.ACACIA_PLANKS,
                Material.DARK_OAK_PLANKS,
                Material.CRIMSON_PLANKS,
                Material.WARPED_PLANKS);
    }

    @Override
    public ShapedRecipe createRecipe(ItemStack item, String key) {
        return new ShapedRecipe(new NamespacedKey(UHCRevamp.getInstance(), key), item);
    }
}
