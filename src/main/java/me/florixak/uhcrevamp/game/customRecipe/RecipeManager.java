package me.florixak.uhcrevamp.game.customRecipe;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XEnchantment;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    private final FileConfiguration recipeConfig;
    private final List<ShapedRecipe> recipesList;

    public RecipeManager(GameManager gameManager) {
        this.recipeConfig = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_RECIPES).getConfig();
        this.recipesList = new ArrayList<>();
    }

    public void registerRecipes() {
        loadRecipes();
    }

    private final String[] pos = {
            "top-left", "top-middle", "top-right",
            "middle-left", "middle", "middle-right",
            "bottom-left", "bottom-middle", "bottom-right"
    };

    private final char[] chars = {
            'A', 'B', 'C',
            'D', 'E', 'F',
            'G', 'H', 'I'
    };

    public ShapedRecipe getRecipe(ItemStack item) {
        for (ShapedRecipe recipe : recipesList) {
            if (recipe.getResult().isSimilar(item)) {
                return recipe;
            }
        }
        return null;
    }

    public List<ShapedRecipe> getRecipesList() {
        return this.recipesList;
    }

    public void loadRecipes() {
        if (recipeConfig.getConfigurationSection("custom-recipes") == null) return;
        ConfigurationSection customRecipesSection = recipeConfig.getConfigurationSection("custom-recipes");

        for (String recipe : customRecipesSection.getKeys(false)) {
            ConfigurationSection recipeSection = customRecipesSection.getConfigurationSection(recipe);
            String itemName = recipeSection.getString("custom-name");
            Material resultMaterial = XMaterial.matchXMaterial(recipe).get().parseMaterial();
            if (itemName == null || itemName.isEmpty())
                itemName = TextUtils.toNormalCamelText(XMaterial.matchXMaterial(resultMaterial).name());
            int amount = recipeSection.getInt("amount");
            ItemStack item = ItemUtils.createItem(resultMaterial, itemName, amount, null);

            ConfigurationSection enchantmentsSection = recipeSection.getConfigurationSection("enchantments");
            if (enchantmentsSection != null) {
                for (String enchant : enchantmentsSection.getKeys(false)) {
                    String enchantmentName = enchant.toUpperCase();
                    Enchantment e = XEnchantment.matchXEnchantment(enchantmentName).get().getEnchant();
                    int level = recipeSection.getInt("enchantments." + enchantmentName, 1);
                    ItemUtils.addEnchant(item, e, level, true);
                }
            }

            ShapedRecipe itemRecipe = new ShapedRecipe(item);

            itemRecipe.shape(
                    "ABC",
                    "DEF",
                    "GHI"
            );

            for (int i = 0; i < pos.length; i++) {
                char ch = this.chars[i];
                String pos = this.pos[i];
                String materialName = recipeConfig.getString("custom-recipes." + recipe + "." + pos);
                Material material = XMaterial.matchXMaterial(materialName.toUpperCase()).get().parseMaterial();
                itemRecipe.setIngredient(ch, material);
                Bukkit.getLogger().info("Material " + material.toString() + " was loaded!");
            }

            this.recipesList.add(itemRecipe);
            Bukkit.addRecipe(itemRecipe);
            Bukkit.getLogger().info("Recipe " + itemRecipe.getResult().toString() + " was loaded! With shape: ");
            Bukkit.getLogger().info(itemRecipe.getIngredientMap().toString());
            Bukkit.getLogger().info(" ");
        }
    }

    public void onDisable() {
        this.recipesList.clear();
    }
}