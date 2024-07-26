package me.florixak.uhcrevamp.game.customRecipes;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XEnchantment;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import me.florixak.uhcrevamp.versions.CustomTags_1_20;
import me.florixak.uhcrevamp.versions.RecipeUtils;
import me.florixak.uhcrevamp.versions.RecipeUtils_1_20;
import me.florixak.uhcrevamp.versions.RecipeUtils_1_8;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class CustomRecipesManager {

    private final FileConfiguration recipeConfig;
    private final List<CustomRecipe> customRecipeList;

    public CustomRecipesManager(GameManager gameManager) {
        this.recipeConfig = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_RECIPES).getConfig();
        this.customRecipeList = new ArrayList<>();
    }

    public void registerRecipes() {
        loadRecipes();
    }

    public CustomRecipe getRecipe(ItemStack item) {
        for (CustomRecipe customRecipe : customRecipeList) {
            if (customRecipe.getResult().isSimilar(item)) {
                return customRecipe;
            }
        }
        return null;
    }

    public List<CustomRecipe> getRecipeList() {
        return this.customRecipeList;
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

            ItemStack[][] matrix = new ItemStack[3][3]; // Assuming a 3x3 crafting grid
            List<String> rows = craftSection.getStringList("shape");
            Map<Character, Material> ingredientMap = new HashMap<>();

            for (String row : craftSection.getConfigurationSection("ingredients").getKeys(false)) {
                String ingredient = craftSection.getString("ingredients." + row).toUpperCase();
                if (ingredient.equals("WOOD_PLANKS")) {
                    ingredientMap.put(row.charAt(0), null); // Placeholder for CustomTags.PLANKS
                } else {
                    ingredientMap.put(row.charAt(0), XMaterial.matchXMaterial(ingredient).get().parseMaterial());
                }
            }

//            Bukkit.getLogger().info("Ingredient Map: " + ingredientMap.toString());

            CustomRecipe customRecipe = new CustomRecipe(result, matrix);
            customRecipeList.add(customRecipe);

            removeRecipe(result.getType());

            if (UHCRevamp.useOldMethods) {
                for (int dataValue = 0; dataValue <= 5; dataValue++) { // 0 to 5 for different wood types
                    for (int i = 0; i < rows.size(); i++) {
                        for (int j = 0; j < rows.get(i).length(); j++) {
                            char ingredientChar = rows.get(i).charAt(j);
                            if (ingredientMap.containsKey(ingredientChar)) {
                                if (ingredientMap.get(ingredientChar) == null) {
                                    matrix[i][j] = new ItemStack(Material.valueOf("WOOD"), 1, (short) dataValue);
                                } else {
                                    matrix[i][j] = new ItemStack(ingredientMap.get(ingredientChar));
                                }
                            } else {
                                matrix[i][j] = null;
                            }
                        }
                    }
                    customRecipe.setShapeMatrix(matrix);
                    RecipeUtils recipeUtils = getRecipeUtils();
                    ShapedRecipe recipe = recipeUtils.createRecipe(result, TextUtils.removeSpecialCharacters(key));
                    recipe.shape(rows.toArray(new String[0]));

                    for (Map.Entry<Character, Material> entry : ingredientMap.entrySet()) {
                        if (entry.getValue() == null) {
                            recipe.setIngredient(entry.getKey(), Material.valueOf("WOOD"), dataValue);
                        } else {
                            recipe.setIngredient(entry.getKey(), entry.getValue());
                        }
                    }

                    Bukkit.addRecipe(recipe);
                }
            } else {
                for (Material plank : CustomTags_1_20.PLANKS.getValues()) {
                    for (int i = 0; i < rows.size(); i++) {
                        for (int j = 0; j < rows.get(i).length(); j++) {
                            char ingredientChar = rows.get(i).charAt(j);
                            if (ingredientMap.containsKey(ingredientChar)) {
                                if (ingredientMap.get(ingredientChar) == null) {
                                    matrix[i][j] = new ItemStack(plank);
                                } else {
                                    matrix[i][j] = new ItemStack(ingredientMap.get(ingredientChar));
                                }
                            } else {
                                matrix[i][j] = null;
                            }
                        }
                    }

                    customRecipe.setShapeMatrix(matrix);
                    RecipeUtils recipeUtils = getRecipeUtils();
                    ShapedRecipe recipe = recipeUtils.createRecipe(result, TextUtils.removeSpecialCharacters(key));
                    recipe.shape(rows.toArray(new String[0]));

                    for (Map.Entry<Character, Material> entry : ingredientMap.entrySet()) {
                        if (entry.getValue() == null) {
                            recipe.setIngredient(entry.getKey(), plank);
                        } else {
                            recipe.setIngredient(entry.getKey(), entry.getValue());
                        }
                    }

                    Bukkit.addRecipe(recipe);
                }
            }
        }
    }

    public void removeRecipe(Material material) {
        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (recipe.getResult().getType().equals(material)) {
                recipes.remove();
            }
        }
    }

    public RecipeUtils getRecipeUtils() {
        if (UHCRevamp.useOldMethods) {
            return new RecipeUtils_1_8();
        } else {
            return new RecipeUtils_1_20();
        }
    }

    public void onDisable() {
        for (CustomRecipe customRecipe : customRecipeList) {
            removeRecipe(customRecipe.getResult().getType());
        }
        this.customRecipeList.clear();
    }
}