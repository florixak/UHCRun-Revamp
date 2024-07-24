package me.florixak.uhcrevamp.utils;

import me.florixak.uhcrevamp.utils.XSeries.XEnchantment;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static ItemStack createItem(Material material, String name, int amount, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        if (name != null) meta.setDisplayName(TextUtils.color(name));
        if (amount == 0) amount = 1;
        if (lore != null && !lore.isEmpty()) meta.setLore(lore);
        item.setItemMeta(meta);
        item.setAmount(amount);
        return item;
    }

    public static boolean hasItemMeta(ItemStack item) {
        return item.hasItemMeta();
    }

    public static void setArmorItemMeta(ItemStack item, Color armorColor) {

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (armorColor != null) meta.setColor(armorColor);
        item.setItemMeta(meta);
    }

    public static void addEnchant(ItemStack item, Enchantment enchantment, int enchantLevel, boolean ignoreLevel) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, enchantLevel, ignoreLevel);
        item.setItemMeta(meta);
    }

    public static void addBookEnchantment(ItemStack item, Enchantment enchantment, int enchantLevel) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(enchantment, enchantLevel, true);
        item.setItemMeta(meta);
    }

    public static void addGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(XEnchantment.UNBREAKING.getEnchant(), 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    public static void removeAttributes(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    public static List<Enchantment> getEnchantments(ItemStack item) {
        List<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            enchantments.add(enchant);
        }
        return enchantments;
    }

    public static void addLore(ItemStack item, String line1, String line2, String line3) {
        List<String> lore = new ArrayList<>();
        if (line1 != null) lore.add(TextUtils.color(line1));
        if (line2 != null) lore.add(TextUtils.color(line2));
        if (line3 != null) lore.add(TextUtils.color(line3));

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static ItemStack createPotionItem(String potionName, int level, int amount, boolean splash) {
        ItemStack potion = new Potion(XPotion.matchXPotion(potionName).get().getPotionType(), level, splash, false).toItemStack(amount);
        return potion;
    }

    public static boolean isPotion(ItemStack item) {
        try {
            Potion.fromItemStack(item);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public static double getAttackDamage(ItemStack item) {
        switch (XMaterial.matchXMaterial(item)) {
            default:
                return 0.25;

            case WOODEN_SHOVEL:
            case GOLDEN_SHOVEL:
            case WOODEN_HOE:
            case GOLDEN_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
                return 1;

            case WOODEN_PICKAXE:
            case GOLDEN_PICKAXE:
            case STONE_SHOVEL:
                return 2;

            case WOODEN_AXE:
            case GOLDEN_AXE:
            case STONE_PICKAXE:
            case IRON_SHOVEL:
                return 3;

            case WOODEN_SWORD:
            case GOLDEN_SWORD:
            case STONE_AXE:
            case IRON_PICKAXE:
            case DIAMOND_SHOVEL:
                return 4;

            case STONE_SWORD:
            case IRON_AXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_SHOVEL:
                return 5;

            case IRON_SWORD:
            case DIAMOND_AXE:
            case NETHERITE_PICKAXE:
                return 6;

            case DIAMOND_SWORD:
            case NETHERITE_AXE:
                return 7;

            case NETHERITE_SWORD:
                return 8;
        }
    }
}