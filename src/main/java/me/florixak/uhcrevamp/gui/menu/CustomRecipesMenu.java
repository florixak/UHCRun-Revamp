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
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class CustomRecipesMenu extends PaginatedMenu {

    private final UHCPlayer uhcPlayer;
    private final CustomRecipeManager recipeManager;
    private final List<CustomRecipe> recipesList;

    public CustomRecipesMenu(MenuUtils menuUtils) {
        super(menuUtils);
        this.uhcPlayer = menuUtils.getUHCPlayer();
        this.recipeManager = GameManager.getGameManager().getRecipeManager();
        this.recipesList = recipeManager.getRecipeList();
    }

    @Override
    public String getMenuName() {
        int pages = recipesList.size() / maxItemsPerPage + 1;
        return TextUtils.color(GameValues.INVENTORY.CUSTOM_RECIPES_TITLE) + " - Page " + (currentPage + 1) + " of " + pages;
    }

    @Override
    public int getSlots() {
        return 6 * GameValues.COLUMNS;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
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

        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * currentPage + i;
            if (index >= recipesList.size()) break;
            if (recipesList.get(index) != null) {
                CustomRecipe recipe = recipesList.get(index);
                inventory.setItem(index, recipe.getResult());
            }
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
