package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.gui.Menu;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CustomRecipeMenu extends Menu {

    private final CustomRecipe recipe;

    public CustomRecipeMenu(MenuUtils menuUtils) {
        super(menuUtils);
        this.recipe = menuUtils.getSelectedRecipe();
    }

    @Override
    public String getMenuName() {
        return TextUtils.color(recipe.getResult().getItemMeta().getDisplayName());
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {
        Material backMaterial = XMaterial.matchXMaterial(GameValues.INVENTORY.BACK_ITEM).get().parseMaterial();
        if (event.getCurrentItem().getType() != backMaterial) return;
        close();
        new CustomRecipesMenu(MenuManager.getMenuUtils(menuUtils.getUHCPlayer())).open();
    }

    @Override
    public void setMenuItems() {
        for (int row = 0; row < 3; row++) {
            int slot = 11 + row + (row * 8);
            for (int col = 0; col < 3; col++) {
                ItemStack item = recipe.getShapeMatrix()[row][col];
                if (item == null) item = new ItemStack(XMaterial.AIR.parseMaterial());
                inventory.setItem(slot + col, item);
            }
        }

        inventory.setItem(24, recipe.getResult());
        inventory.setItem(36, ItemUtils.createItem(XMaterial.matchXMaterial(GameValues.INVENTORY.BACK_ITEM).get().parseMaterial(), "&cBack", 1, null));
    }
}
