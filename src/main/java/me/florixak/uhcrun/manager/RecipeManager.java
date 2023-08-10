package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XEnchantment;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    private final FileConfiguration recipe_config;

    private List<ShapedRecipe> recipes;

    public RecipeManager(GameManager gameManager) {
        this.recipe_config = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_RECIPES).getConfig();
        this.recipes = new ArrayList<>();
    }

    public void registerRecipes() {

        if (recipe_config.getConfigurationSection("custom-recipes") == null) return;

        for (String recipe : recipe_config.getConfigurationSection("custom-recipes").getKeys(false)) {
            ConfigurationSection recipeSection = recipe_config.getConfigurationSection("custom-recipes." + recipe);
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

            ShapedRecipe itemRecipe = new ShapedRecipe(new NamespacedKey(UHCRun.getInstance(), recipe.toLowerCase()), item);

            itemRecipe.shape("ABC", "DEF", "GHI");

            itemRecipe.setIngredient('A', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".top-left", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('B', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".top-middle", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('C', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".top-right", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('D', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".middle-left", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('E', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".middle", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('F', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".middle-right", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('G', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".bottom-left", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('H', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".bottom-middle", "BARRIER").toUpperCase()).get().parseMaterial());
            itemRecipe.setIngredient('I', XMaterial.matchXMaterial(
                    recipe_config.getString("custom-recipes." + recipe + ".bottom-right", "BARRIER").toUpperCase()).get().parseMaterial());

            Bukkit.getServer().addRecipe(itemRecipe);
            this.recipes.add(itemRecipe);
        }
    }
}