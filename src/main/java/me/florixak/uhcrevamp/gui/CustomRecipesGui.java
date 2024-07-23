package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customRecipe.RecipeManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CustomRecipesGui extends Gui {

    public CustomRecipesGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 5 * GameValues.COLUMNS, GameValues.INVENTORY.CUSTOM_RECIPES_TITLE);
    }

    @Override
    public void init() {
        super.init();
        RecipeManager recipeManager = gameManager.getRecipeManager();
        int slot = 0;

        for (ShapedRecipe recipe : recipeManager.getRecipesList()) {
            if (slot >= inventory.getSize()) break;

            // Create an ItemStack for the recipe result
            ItemStack resultItem = recipe.getResult();
            // Optionally, customize the ItemStack to include recipe details in its lore

            inventory.setItem(slot++, resultItem);
            // Repeat for each recipe, incrementing the slot each time
        }
    }

    @Override
    public void open() {
        // TODO if custom recipes are enabled
        super.open();
    }

}
