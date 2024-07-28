package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipeManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.gui.PaginatedMenu;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class CustomRecipesMenu extends PaginatedMenu {

    private final UHCPlayer uhcPlayer;
    private final CustomRecipeManager recipeManager;
    private final List<CustomRecipe> recipesList;

    public CustomRecipesMenu(MenuUtils menuUtils) {
        super(menuUtils, GameValues.INVENTORY.CUSTOM_RECIPES_TITLE);
        this.uhcPlayer = menuUtils.getUHCPlayer();
        this.recipeManager = GameManager.getGameManager().getRecipeManager();
        this.recipesList = recipeManager.getRecipeList();
    }

    @Override
    public int getSlots() {
        return GameValues.INVENTORY.CUSTOM_RECIPES_SLOTS;
    }

    @Override
    public int getItemsCount() {
        return recipesList.size();
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {
        if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
            close();
        } else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
            handlePaging(event, recipesList);
        } else {
            CustomRecipe selectedRecipe = recipeManager.getRecipe(event.getCurrentItem());
            menuUtils.setSelectedRecipe(selectedRecipe);
            close();
            new CustomRecipeMenu(menuUtils).open();
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        for (int i = getStartIndex(); i < getEndIndex(); i++) {
            CustomRecipe recipe = recipesList.get(i);
            inventory.setItem(i - getStartIndex(), recipe.getResult());
        }
    }

    @Override
    public void open() {
        if (!GameValues.KITS.ENABLED) {
            uhcPlayer.sendMessage(Messages.KITS_DISABLED.toString());
            return;
        }
        super.open();
    }
}
