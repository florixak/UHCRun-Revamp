package me.florixak.uhcrevamp.game.customCrafts;

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
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class CraftManager {

    private final FileConfiguration recipeConfig;
    private final List<CustomCraft> customCraftList;

    public CraftManager(GameManager gameManager) {
        this.recipeConfig = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_RECIPES).getConfig();
        this.customCraftList = new ArrayList<>();
    }

    public void registerRecipes() {
        loadRecipes();
    }

    public CustomCraft getRecipe(ItemStack item) {
        for (CustomCraft customCraft : customCraftList) {
            if (customCraft.getResult().isSimilar(item)) {
                return customCraft;
            }
        }
        return null;
    }

    public List<CustomCraft> getCustomCrafts() {
        return this.customCraftList;
    }

    public void loadRecipes() {
        if (recipeConfig.getConfigurationSection("custom-recipes") == null) return;
        ConfigurationSection customRecipesSection = recipeConfig.getConfigurationSection("custom-recipes");
        for (String key : customRecipesSection.getKeys(false)) {
            ConfigurationSection craftSection = customRecipesSection.getConfigurationSection(key);
            Material material = XMaterial.matchXMaterial(craftSection.getString("result").toUpperCase()).get().parseMaterial();
            int amount = !craftSection.contains("amount") ? 1 : craftSection.getInt("amount");
            ItemStack result = ItemUtils.createItem(material, TextUtils.color(key), amount, null);

            if (craftSection.getConfigurationSection("enchantments") != null) {
                ConfigurationSection enchantmentsSection = craftSection.getConfigurationSection("enchantments");
                for (String enchantment : enchantmentsSection.getKeys(false)) {
                    Enchantment enchant = XEnchantment.matchXEnchantment(enchantment).get().getEnchant();
                    int level = enchantmentsSection.getInt(enchantment);
                    result.addUnsafeEnchantment(enchant, level);
                }
            }

            if (material == Material.STONE_PICKAXE) {
                Iterator<Recipe> recipes = Bukkit.recipeIterator();
                while (recipes.hasNext()) {
                    if (recipes.next().getResult().getType().equals(Material.WOOD_PICKAXE)) {
                        recipes.remove();
                    }
                }
            } else if (material == Material.IRON_PICKAXE) {
                Iterator<Recipe> recipes = Bukkit.recipeIterator();
                while (recipes.hasNext()) {
                    if (recipes.next().getResult().getType().equals(Material.STONE_PICKAXE)) {
                        recipes.remove();
                    }
                }
            }


            ItemStack[][] matrix = new ItemStack[3][3]; // Assuming a 3x3 crafting grid
            List<String> rows = craftSection.getStringList("shape");
            Map<Character, Material> ingredientMap = new HashMap<>();

            for (String row : craftSection.getConfigurationSection("ingredients").getKeys(false)) {
                ingredientMap.put(row.charAt(0), XMaterial.matchXMaterial(craftSection.getString("ingredients." + row).toUpperCase()).get().parseMaterial());
            }

            for (int i = 0; i < rows.size(); i++) {
                for (int j = 0; j < rows.get(i).length(); j++) {
                    char ingredientChar = rows.get(i).charAt(j);
                    if (ingredientMap.containsKey(ingredientChar)) {
                        matrix[i][j] = new ItemStack(ingredientMap.get(ingredientChar));
                    } else {
                        matrix[i][j] = null;
                    }
                }
            }

            CustomCraft customCraft = new CustomCraft(result, matrix);
            customCraftList.add(customCraft);

            ShapedRecipe recipe = new ShapedRecipe(result);
            recipe.shape(rows.toArray(new String[0]));

            for (Map.Entry<Character, Material> entry : ingredientMap.entrySet()) {
                recipe.setIngredient(entry.getKey(), entry.getValue());
            }

            Bukkit.addRecipe(recipe);
        }
    }

    public void onDisable() {
        this.customCraftList.clear();
    }
}