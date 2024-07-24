package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customCrafts.CraftManager;
import me.florixak.uhcrevamp.game.customCrafts.CustomCraft;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.inventory.ItemStack;

public class CustomRecipesGui extends Gui {

    public CustomRecipesGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 5 * GameValues.COLUMNS, GameValues.INVENTORY.CUSTOM_RECIPES_TITLE);
    }

    @Override
    public void init() {
        super.init();
        CraftManager craftManager = gameManager.getCraftManager();
        int slot = 0;

        for (CustomCraft recipe : craftManager.getCustomCrafts()) {
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
