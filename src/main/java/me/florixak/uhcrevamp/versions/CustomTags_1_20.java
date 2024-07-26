package me.florixak.uhcrevamp.versions;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.EnumSet;
import java.util.Set;

public class CustomTags_1_20 {
    public static final CustomTag PLANKS = new CustomTag() {

        @Override
        public boolean isTagged(Material material) {
            return material == Material.OAK_PLANKS ||
                    material == Material.SPRUCE_PLANKS ||
                    material == Material.BIRCH_PLANKS ||
                    material == Material.JUNGLE_PLANKS ||
                    material == Material.ACACIA_PLANKS ||
                    material == Material.DARK_OAK_PLANKS ||
                    material == Material.CRIMSON_PLANKS ||
                    material == Material.WARPED_PLANKS;
        }

        @Override
        public Set<Material> getValues() {
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
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("planks");
        }
    };
}
