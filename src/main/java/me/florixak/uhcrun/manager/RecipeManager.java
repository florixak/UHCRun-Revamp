package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XEnchantment;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    private final FileConfiguration recipeConfig;
    private List<ShapedRecipe> recipes;

    public RecipeManager(GameManager gameManager) {
        this.recipeConfig = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_RECIPES).getConfig();
        this.recipes = new ArrayList<>();
    }

    public void registerRecipes() {
        loadRecipes();
        for (ShapedRecipe recipe : recipes) {
            Bukkit.getServer().addRecipe(recipe);
            Bukkit.getLogger().info("Recipe " + recipe.toString() + " was registered!");
        }
    }

    public void loadRecipes() {

        if (recipeConfig.getConfigurationSection("custom-recipes") == null) return;

        for (String recipe : recipeConfig.getConfigurationSection("custom-recipes").getKeys(false)) {
            ConfigurationSection recipeSection = recipeConfig.getConfigurationSection("custom-recipes." + recipe);
            String itemName = recipe.toUpperCase();
            int amount = recipeSection.getInt("amount");
            ItemStack item = new ItemStack(XMaterial.matchXMaterial(itemName).get().parseMaterial(), amount);

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

            itemRecipe.setIngredient('A', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".top-left").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('B', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".top-middle").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('C', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".top-right").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('D', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".middle-left").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('E', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".middle").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('F', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".middle-right").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('G', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".bottom-left").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('H', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".bottom-middle").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('I', XMaterial.matchXMaterial(
                    recipeConfig.getString("custom-recipes." + recipe + ".bottom-right").toUpperCase()).get().parseMaterial());

            this.recipes.add(itemRecipe);
        }
    }
}