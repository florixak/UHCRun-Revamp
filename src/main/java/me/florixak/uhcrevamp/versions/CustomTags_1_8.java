package me.florixak.uhcrevamp.versions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class CustomTags_1_8 {

    public static final CustomTag PLANKS = new CustomTag() {

        @Override
        public boolean isTagged(Material material) {
            return material == Material.valueOf("WOOD");
        }

        @Override
        public Set<Material> getValues() {
            return EnumSet.of(Material.valueOf("WOOD"));
        }

        @Override
        public String getKey() {
            return "minecraft:planks";
        }

        public boolean isSpecificWoodType(ItemStack item, int dataValue) {
            return item.getType() == Material.valueOf("WOOD") && item.getDurability() == (short) dataValue;
        }
    };
}
