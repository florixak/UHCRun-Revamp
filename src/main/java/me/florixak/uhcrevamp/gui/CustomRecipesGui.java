package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomRecipesGui extends Gui {

    private final List<CustomRecipe> loadedRecipes;

    public CustomRecipesGui(GameManager gameManager, UHCPlayer uhcPlayer, List<CustomRecipe> recipes) {
        super(gameManager, uhcPlayer, 5 * GameValues.COLUMNS, GameValues.INVENTORY.CUSTOM_RECIPES_TITLE);
        this.loadedRecipes = recipes;
    }

    @Override
    public void init() {
        super.init();

        int slot = 0;
        for (CustomRecipe recipe : loadedRecipes) {
            if (slot >= inventory.getSize()) break;
            ItemStack resultItem = recipe.getResult();
            inventory.setItem(slot++, resultItem);
        }
    }

    @Override
    public void open() {
        // TODO if custom recipes are enabled
        super.open();
    }
}
