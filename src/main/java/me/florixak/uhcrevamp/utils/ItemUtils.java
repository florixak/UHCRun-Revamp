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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

	/**
	 * Creates an ItemStack with the specified material, name, amount, and lore.
	 *
	 * @param material The material of the item.
	 * @param name     The display name of the item.
	 * @param amount   The amount of the item.
	 * @param lore     The lore of the item.
	 * @return The created ItemStack.
	 */
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

	/**
	 * Checks if the given ItemStack has item meta.
	 *
	 * @param item The ItemStack to check.
	 * @return True if the item has meta, false otherwise.
	 */
	public static boolean hasItemMeta(ItemStack item) {
		return item.hasItemMeta();
	}

	/**
	 * Sets the color of a leather armor item.
	 *
	 * @param item       The leather armor ItemStack.
	 * @param armorColor The color to set.
	 */
	public static void setArmorItemMeta(ItemStack item, Color armorColor) {
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		if (armorColor != null) meta.setColor(armorColor);
		item.setItemMeta(meta);
	}

	/**
	 * Adds an unsafe enchantment to an ItemStack.
	 *
	 * @param item         The ItemStack to enchant.
	 * @param enchantment  The enchantment to add.
	 * @param enchantLevel The level of the enchantment.
	 */
	public static void addEnchant(ItemStack item, Enchantment enchantment, int enchantLevel) {
		item.addUnsafeEnchantment(enchantment, enchantLevel);
	}

	/**
	 * Adds an enchantment to an enchanted book ItemStack.
	 *
	 * @param item         The enchanted book ItemStack.
	 * @param enchantment  The enchantment to add.
	 * @param enchantLevel The level of the enchantment.
	 */
	public static void addBookEnchantment(ItemStack item, Enchantment enchantment, int enchantLevel) {
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addStoredEnchant(enchantment, enchantLevel, true);
		item.setItemMeta(meta);
	}

	/**
	 * Adds a glow effect to an ItemStack.
	 *
	 * @param item The ItemStack to add the glow effect to.
	 */
	public static void addGlow(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(XEnchantment.UNBREAKING.getEnchant(), 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
	}

	/**
	 * Removes attributes from an ItemStack.
	 *
	 * @param item The ItemStack to remove attributes from.
	 */
	public static void removeAttributes(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
	}

	/**
	 * Gets the enchantments of an ItemStack.
	 *
	 * @param item The ItemStack to get enchantments from.
	 * @return A list of enchantments on the item.
	 */
	public static List<Enchantment> getEnchantments(ItemStack item) {
		List<Enchantment> enchantments = new ArrayList<>();
		for (Enchantment enchant : item.getEnchantments().keySet()) {
			enchantments.add(enchant);
		}
		return enchantments;
	}

	/**
	 * Adds lore to an ItemStack.
	 *
	 * @param item  The ItemStack to add lore to.
	 * @param line1 The first line of lore.
	 * @param line2 The second line of lore.
	 * @param line3 The third line of lore.
	 */
	public static void addLore(ItemStack item, String line1, String line2, String line3) {
		List<String> lore = new ArrayList<>();
		if (line1 != null) lore.add(TextUtils.color(line1));
		if (line2 != null) lore.add(TextUtils.color(line2));
		if (line3 != null) lore.add(TextUtils.color(line3));

		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	/**
	 * Creates a potion ItemStack.
	 *
	 * @param splash     Determines if the potion is a splash potion.
	 * @param effectType The type of potion effect.
	 * @param customName The name of the potion.
	 * @param duration   The duration of the effect in seconds.
	 * @param amplifier  The amplifier of the effect (starting from 1).
	 * @return The created potion ItemStack.
	 */
	public static ItemStack createPotionItem(PotionEffectType effectType, String customName, int amount, int duration, int amplifier, boolean splash) {
		Material potionMaterial = splash ? XMaterial.SPLASH_POTION.parseMaterial() : XMaterial.POTION.parseMaterial();
		ItemStack potion = new ItemStack(potionMaterial, amount);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();

		if (meta != null) {
			if (customName != null && !customName.isEmpty()) {
				meta.setDisplayName(TextUtils.color("&f" + customName));
				ArrayList<String> lore = new ArrayList<>();
				lore.add(TextUtils.color("&7" + TextUtils.toNormalCamelText(XPotion.matchXPotion(effectType).name()) + " " + (amplifier) + " (" + (duration) + "s)"));
				meta.setLore(lore);
			} else
				meta.setDisplayName(TextUtils.color("&f" + TextUtils.toNormalCamelText(XPotion.matchXPotion(effectType).name()) + " " + (amplifier) + " (" + (duration) + "s)"));
			meta.addCustomEffect(XPotion.matchXPotion(effectType).buildPotionEffect(duration * 20, amplifier), true);
			potion.setItemMeta(meta);
		}

		return potion;
	}

	/**
	 * Checks if an ItemStack is a potion.
	 *
	 * @param item The ItemStack to check.
	 * @return True if the item is a potion, false otherwise.
	 */
	public static boolean isPotion(ItemStack item) {
		if (item == null) {
			return false;
		}
		Material type = XMaterial.matchXMaterial(item).parseMaterial();
		return type == XMaterial.POTION.parseMaterial() || type == XMaterial.SPLASH_POTION.parseMaterial() || type == XMaterial.LINGERING_POTION.parseMaterial();
	}

	/**
	 * Gets the color associated with a potion effect type for Minecraft 1.8.8.
	 *
	 * @param effectType The potion effect type.
	 * @return The color associated with the potion effect type.
	 */
	public static Color getPotionColor(PotionEffectType effectType) {
		if (effectType == null) return Color.WHITE;

		switch (effectType.getName().toUpperCase()) {
			case "SPEED":
				return Color.fromRGB(8171462);
			case "SLOWNESS":
				return Color.fromRGB(5926017);
			case "HASTE":
				return Color.fromRGB(14270531);
			case "MINING_FATIGUE":
				return Color.fromRGB(4866583);
			case "STRENGTH":
				return Color.fromRGB(9643043);
			case "INSTANT_HEALTH":
				return Color.fromRGB(16262179);
			case "INSTANT_DAMAGE":
				return Color.fromRGB(4393481);
			case "JUMP_BOOST":
				return Color.fromRGB(2293580);
			case "NAUSEA":
				return Color.fromRGB(5578058);
			case "REGENERATION":
				return Color.fromRGB(13458603);
			case "RESISTANCE":
				return Color.fromRGB(10044730);
			case "FIRE_RESISTANCE":
				return Color.fromRGB(14981690);
			case "WATER_BREATHING":
				return Color.fromRGB(3035801);
			case "INVISIBILITY":
				return Color.fromRGB(8356754);
			case "BLINDNESS":
				return Color.fromRGB(2039587);
			case "NIGHT_VISION":
				return Color.fromRGB(2039713);
			case "HUNGER":
				return Color.fromRGB(5797459);
			case "WEAKNESS":
				return Color.fromRGB(4738376);
			case "POISON":
				return Color.fromRGB(5149489);
			case "WITHER":
				return Color.fromRGB(3484199);
			case "HEALTH_BOOST":
				return Color.fromRGB(16284963);
			case "ABSORPTION":
				return Color.fromRGB(2445989);
			case "SATURATION":
				return Color.fromRGB(16262179);
			default:
				return Color.WHITE;
		}
	}

	/**
	 * Gets the attack damage of an ItemStack.
	 *
	 * @param item The ItemStack to get attack damage from.
	 * @return The attack damage of the item.
	 */
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