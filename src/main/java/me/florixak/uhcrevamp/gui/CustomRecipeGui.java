package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

public class CustomRecipeGui extends Gui {

    private final CustomRecipe recipe;

    public CustomRecipeGui(GameManager gameManager, UHCPlayer uhcPlayer, CustomRecipe recipe) {
        super(gameManager, uhcPlayer, 5 * GameValues.COLUMNS, TextUtils.color("Recipe: " + recipe.getResult().getItemMeta().getDisplayName()));
        this.recipe = recipe;
    }

    @Override
    public void init() {
        super.init();

        for (int row = 0; row < 3; row++) {
            int slot = 11 + row + (row * 8);
            for (int col = 0; col < 3; col++) {
                ItemStack item = recipe.getShapeMatrix()[row][col];
                if (item == null) item = new ItemStack(XMaterial.AIR.parseMaterial());
                setItem(slot + col, item);
            }
        }

        setItem(24, recipe.getResult());
        setItem(36, ItemUtils.createItem(XMaterial.BARRIER.parseMaterial(), "&cBack", 1, null));
    }

    @Override
    public void open() {
        super.open();
    }
}