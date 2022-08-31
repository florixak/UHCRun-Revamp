package me.florixak.uhcrun.utility;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    public static ItemStack item(ItemStack item, String name, int amount, boolean glow){
        ItemMeta meta = item.getItemMeta();
        if (name != null) meta.setDisplayName(TextUtil.color(name));
        if (amount == 0) amount = 1;
        if (glow == true) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        item.setAmount(amount);
        return item;
    }

    public static void setArmorItemMeta(ItemStack item, String name, Color armorColor){

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (name != null) meta.setDisplayName(TextUtil.color(name));
        if (armorColor != null) meta.setColor(armorColor);
        item.setItemMeta(meta);
    }

    public static void addEnchant(ItemStack item, Enchantment enchantment, int enchantLevel, boolean ignoreLevel){
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, enchantLevel, ignoreLevel);
        item.setItemMeta(meta);
    }

    public static void addBookEnchantment(ItemStack item, Enchantment enchantment, int enchantLevel){
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(enchantment, enchantLevel, true);
        item.setItemMeta(meta);
    }

    public static void addGlow(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    public static void removeAttributes(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    public static void addLore(ItemStack item, String line1, String line2, String line3){
        List<String> lore = new ArrayList<>();
        if (line1 != null) lore.add(TextUtil.color(line1));
        if (line2 != null) lore.add(TextUtil.color(line2));
        if (line3 != null) lore.add(TextUtil.color(line3));

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}