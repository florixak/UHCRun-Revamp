package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;

public class RecipeManager {

    public static int recipes = 0;
    public HashMap<String, Material> recipe = new HashMap<>();

    public RecipeManager() {
        goldenApple();
        anvil();
        fishingRod();
        woodenTools();
        stoneTools();
        goldenTools();
        ironTools();
        diamondTools();
    }

    public void goldenApple() {

        ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE);
        ShapedRecipe shapedRecipe = new ShapedRecipe(NamespacedKey.minecraft("light-gapple"), itemStack);

        shapedRecipe.shape(" G ", "GAG", " G ");
        shapedRecipe.setIngredient('G', Material.GOLD_INGOT);
        shapedRecipe.setIngredient('A', Material.APPLE);

        Bukkit.addRecipe(shapedRecipe);
        recipe.put("Golden Apple", Material.GOLDEN_APPLE);
        recipes += 1;

    }

    public void anvil() {

        ItemStack itemStack = new ItemStack(Material.ANVIL);
        ShapedRecipe shapedRecipe = new ShapedRecipe(NamespacedKey.minecraft("light-anvil"), itemStack);

        shapedRecipe.shape("III", " B ", "III");
        shapedRecipe.setIngredient('B', Material.IRON_BLOCK);
        shapedRecipe.setIngredient('I', Material.IRON_INGOT);

        Bukkit.addRecipe(shapedRecipe);
        recipe.put("Light Anvil", Material.ANVIL);
        recipes += 1;
    }

    public void fishingRod() {

        ItemStack itemStack = new ItemStack(Material.FISHING_ROD);
        ShapedRecipe shapedRecipe = new ShapedRecipe(NamespacedKey.minecraft("light-rod"), itemStack);

        shapedRecipe.shape("  T", " TS", "T  ");
        shapedRecipe.setIngredient('T', Material.STICK);
        shapedRecipe.setIngredient('S', Material.STRING);

        Bukkit.addRecipe(shapedRecipe);
        recipe.put("Light Fishing Rod", Material.FISHING_ROD);
        recipes += 1;
    }

    public void woodenTools() {

        ItemStack itemStack1 = new ItemStack(XMaterial.WOODEN_AXE.parseItem());
        ItemUtils.addEnchant(itemStack1, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack1, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack2 = new ItemStack(XMaterial.WOODEN_PICKAXE.parseItem());
        ItemUtils.addEnchant(itemStack2, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack2, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack3 = new ItemStack(XMaterial.WOODEN_SHOVEL.parseItem());
        ItemUtils.addEnchant(itemStack3, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack3, Enchantment.DURABILITY, 3, false);

        ShapedRecipe shapedRecipe1 = new ShapedRecipe(NamespacedKey.minecraft("wood-axe"), itemStack1);
        shapedRecipe1.shape("WW ", "WS ", " S ");
        shapedRecipe1.setIngredient('W', XMaterial.OAK_PLANKS.parseMaterial());
        shapedRecipe1.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe2 = new ShapedRecipe(NamespacedKey.minecraft("wood-pickaxe"), itemStack2);
        shapedRecipe2.shape("WWW", " S ", " S ");
        shapedRecipe2.setIngredient('W', XMaterial.OAK_PLANKS.parseMaterial());
        shapedRecipe2.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe3 = new ShapedRecipe(NamespacedKey.minecraft("wood-shovel"), itemStack3);
        shapedRecipe3.shape(" W ", " S ", " S ");
        shapedRecipe3.setIngredient('W', XMaterial.OAK_PLANKS.parseMaterial());
        shapedRecipe3.setIngredient('S', XMaterial.STICK.parseMaterial());


        Bukkit.addRecipe(shapedRecipe1);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe2);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe3);
        recipes += 1;
    }
    public void stoneTools() {

        ItemStack itemStack1 = new ItemStack(XMaterial.STONE_AXE.parseItem());
        ItemUtils.addEnchant(itemStack1, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack1, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack2 = new ItemStack(XMaterial.STONE_PICKAXE.parseItem());
        ItemUtils.addEnchant(itemStack2, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack2, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack3 = new ItemStack(XMaterial.STONE_SHOVEL.parseItem());
        ItemUtils.addEnchant(itemStack3, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack3, Enchantment.DURABILITY, 3, false);

        ShapedRecipe shapedRecipe1 = new ShapedRecipe(NamespacedKey.minecraft("stone-axe"), itemStack1);
        shapedRecipe1.shape("CC ", "CS ", " S ");
        shapedRecipe1.setIngredient('C', XMaterial.COBBLESTONE.parseMaterial());
        shapedRecipe1.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe2 = new ShapedRecipe(NamespacedKey.minecraft("stone-pickaxe"), itemStack2);
        shapedRecipe2.shape("CCC", " S ", " S ");
        shapedRecipe2.setIngredient('C', XMaterial.COBBLESTONE.parseMaterial());
        shapedRecipe2.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe3 = new ShapedRecipe(NamespacedKey.minecraft("stone-shovel"), itemStack3);
        shapedRecipe3.shape(" C ", " S ", " S ");
        shapedRecipe3.setIngredient('C', XMaterial.COBBLESTONE.parseMaterial());
        shapedRecipe3.setIngredient('S', XMaterial.STICK.parseMaterial());


        Bukkit.addRecipe(shapedRecipe1);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe2);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe3);
        recipes += 1;
    }
    public void goldenTools() {

        ItemStack itemStack1 = new ItemStack(XMaterial.GOLDEN_AXE.parseItem());
        ItemUtils.addEnchant(itemStack1, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack1, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack2 = new ItemStack(XMaterial.GOLDEN_PICKAXE.parseItem());
        ItemUtils.addEnchant(itemStack2, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack2, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack3 = new ItemStack(XMaterial.GOLDEN_SHOVEL.parseItem());
        ItemUtils.addEnchant(itemStack3, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack3, Enchantment.DURABILITY, 3, false);

        ShapedRecipe shapedRecipe1 = new ShapedRecipe(NamespacedKey.minecraft("gold-axe"), itemStack1);
        shapedRecipe1.shape("GG ", "GS ", " S ");
        shapedRecipe1.setIngredient('G', XMaterial.GOLD_INGOT.parseMaterial());
        shapedRecipe1.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe2 = new ShapedRecipe(NamespacedKey.minecraft("gold-pickaxe"), itemStack2);
        shapedRecipe2.shape("GGG", " S ", " S ");
        shapedRecipe2.setIngredient('G', XMaterial.GOLD_INGOT.parseMaterial());
        shapedRecipe2.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe3 = new ShapedRecipe(NamespacedKey.minecraft("gold-shovel"), itemStack3);
        shapedRecipe3.shape(" G ", " S ", " S ");
        shapedRecipe3.setIngredient('G', XMaterial.GOLD_INGOT.parseMaterial());
        shapedRecipe3.setIngredient('S', XMaterial.STICK.parseMaterial());

        Bukkit.addRecipe(shapedRecipe1);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe2);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe3);
        recipes += 1;
    }
    public void ironTools() {

        ItemStack itemStack1 = new ItemStack(XMaterial.IRON_AXE.parseItem());
        ItemUtils.addEnchant(itemStack1, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack1, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack2 = new ItemStack(XMaterial.IRON_PICKAXE.parseItem());
        ItemUtils.addEnchant(itemStack2, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack2, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack3 = new ItemStack(XMaterial.IRON_SHOVEL.parseItem());
        ItemUtils.addEnchant(itemStack3, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack3, Enchantment.DURABILITY, 3, false);

        ShapedRecipe shapedRecipe1 = new ShapedRecipe(NamespacedKey.minecraft("iron-axe"), itemStack1);
        shapedRecipe1.shape("II ", "IS ", " S ");
        shapedRecipe1.setIngredient('I', XMaterial.IRON_INGOT.parseMaterial());
        shapedRecipe1.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe2 = new ShapedRecipe(NamespacedKey.minecraft("iron-pickaxe"), itemStack2);
        shapedRecipe2.shape("III", " S ", " S ");
        shapedRecipe2.setIngredient('I', XMaterial.IRON_INGOT.parseMaterial());
        shapedRecipe2.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe3 = new ShapedRecipe(NamespacedKey.minecraft("iron-shovel"), itemStack3);
        shapedRecipe3.shape(" I ", " S ", " S ");
        shapedRecipe3.setIngredient('I', XMaterial.IRON_INGOT.parseMaterial());
        shapedRecipe3.setIngredient('S', XMaterial.STICK.parseMaterial());

        Bukkit.addRecipe(shapedRecipe1);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe2);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe3);
        recipes += 1;
    }
    public void diamondTools() {

        ItemStack itemStack1 = new ItemStack(XMaterial.DIAMOND_AXE.parseItem());
        ItemUtils.addEnchant(itemStack1, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack1, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack2 = new ItemStack(XMaterial.DIAMOND_PICKAXE.parseItem());
        ItemUtils.addEnchant(itemStack2, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack2, Enchantment.DURABILITY, 3, false);

        ItemStack itemStack3 = new ItemStack(XMaterial.DIAMOND_SHOVEL.parseItem());
        ItemUtils.addEnchant(itemStack3, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(itemStack3, Enchantment.DURABILITY, 3, false);

        ShapedRecipe shapedRecipe1 = new ShapedRecipe(NamespacedKey.minecraft("diamond-axe"), itemStack1);
        shapedRecipe1.shape("DD ", "DS ", " S ");
        shapedRecipe1.setIngredient('D', XMaterial.DIAMOND.parseMaterial());
        shapedRecipe1.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe2 = new ShapedRecipe(NamespacedKey.minecraft("diamond-pickaxe"), itemStack2);
        shapedRecipe2.shape("DDD", " S ", " S ");
        shapedRecipe2.setIngredient('D', XMaterial.DIAMOND.parseMaterial());
        shapedRecipe2.setIngredient('S', XMaterial.STICK.parseMaterial());

        ShapedRecipe shapedRecipe3 = new ShapedRecipe(NamespacedKey.minecraft("diamond-shovel"), itemStack3);
        shapedRecipe3.shape(" D ", " S ", " S ");
        shapedRecipe3.setIngredient('D', XMaterial.DIAMOND.parseMaterial());
        shapedRecipe3.setIngredient('S', XMaterial.STICK.parseMaterial());

        Bukkit.addRecipe(shapedRecipe1);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe2);
        recipes += 1;
        Bukkit.addRecipe(shapedRecipe3);
        recipes += 1;
    }
//    public void netheriteTools() {
//
//        ItemStack itemStack1 = new ItemStack(XMaterial.NETHERITE_AXE.parseItem());
//        ItemUtil.addEnchant(itemStack1, Enchantment.DIG_SPEED, 3, false);
//        ItemUtil.addEnchant(itemStack1, Enchantment.DURABILITY, 3, false);
//
//        ItemStack itemStack2 = new ItemStack(XMaterial.NETHERITE_PICKAXE.parseItem());
//        ItemUtil.addEnchant(itemStack2, Enchantment.DIG_SPEED, 3, false);
//        ItemUtil.addEnchant(itemStack2, Enchantment.DURABILITY, 3, false);
//
//        ItemStack itemStack3 = new ItemStack(XMaterial.NETHERITE_SHOVEL.parseItem());
//        ItemUtil.addEnchant(itemStack3, Enchantment.DIG_SPEED, 3, false);
//        ItemUtil.addEnchant(itemStack3, Enchantment.DURABILITY, 3, false);
//
//        ShapedRecipe shapedRecipe1 = new ShapedRecipe(NamespacedKey.minecraft("netherite-axe"), itemStack1);
//        shapedRecipe1.shape("NN ", "NS ", " S ");
//        shapedRecipe1.setIngredient('N', XMaterial.NETHERITE_INGOT.parseMaterial());
//        shapedRecipe1.setIngredient('S', XMaterial.STICK.parseMaterial());
//
//        ShapedRecipe shapedRecipe2 = new ShapedRecipe(NamespacedKey.minecraft("netherite-pickaxe"), itemStack2);
//        shapedRecipe2.shape("NNN", " S ", " S ");
//        shapedRecipe2.setIngredient('N', XMaterial.NETHERITE_INGOT.parseMaterial());
//        shapedRecipe2.setIngredient('S', XMaterial.STICK.parseMaterial());
//
//        ShapedRecipe shapedRecipe3 = new ShapedRecipe(NamespacedKey.minecraft("netherite-shovel"), itemStack3);
//        shapedRecipe3.shape(" N ", " S ", " S ");
//        shapedRecipe3.setIngredient('N', XMaterial.NETHERITE_INGOT.parseMaterial());
//        shapedRecipe3.setIngredient('S', XMaterial.STICK.parseMaterial());
//
//        Bukkit.addRecipe(shapedRecipe1);
//        recipes += 1;
//        Bukkit.addRecipe(shapedRecipe2);
//        recipes += 1;
//        Bukkit.addRecipe(shapedRecipe3);
//        recipes += 1;
//    }
}
