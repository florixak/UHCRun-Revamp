package me.florixak.uhcrevamp.game.customCrafts;

import org.bukkit.inventory.ItemStack;

public class CustomCraft {

    private final ItemStack result;
    private final ItemStack[][] shapeMatrix;

    public CustomCraft(ItemStack result, ItemStack[][] shapeMatrix) {
        this.result = result;
        this.shapeMatrix = shapeMatrix;
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack[][] getShapeMatrix() {
        return shapeMatrix;
    }

    public boolean matches(ItemStack[][] craftingMatrix) {
        // Ensure matrices are the same size
        if (craftingMatrix.length != shapeMatrix.length || craftingMatrix[0].length != shapeMatrix[0].length) {
            return false;
        }

        for (int i = 0; i < shapeMatrix.length; i++) {
            for (int j = 0; j < shapeMatrix[i].length; j++) {
                ItemStack ingredientItem = shapeMatrix[i][j];
                ItemStack craftingItem = craftingMatrix[i][j];

                // Check if both are null (empty slot)
                if (ingredientItem == null && craftingItem == null) {
                    continue; // Correct match, move to next slot
                }

                // If one is null and the other isn't, or if they don't match, return false
                if ((ingredientItem == null && craftingItem != null) || (ingredientItem != null && craftingItem == null) || !ingredientItem.isSimilar(craftingItem)) {
                    return false;
                }
            }
        }

        // All items matched
        return true;
    }
}
