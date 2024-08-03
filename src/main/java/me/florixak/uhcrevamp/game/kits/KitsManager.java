package me.florixak.uhcrevamp.game.kits;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XEnchantment;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class KitsManager {

	private final FileConfiguration config, kitsConfig;
	private final List<Kit> kitsList;

	public KitsManager(final GameManager gameManager) {
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		this.kitsConfig = gameManager.getConfigManager().getFile(ConfigType.KITS).getConfig();

		this.kitsList = new ArrayList<>();
	}

	public void loadKits() {
		if (!GameValues.KITS.ENABLED) return;

		final ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
		for (final String kitName : kitsSection.getKeys(false)) {

			List<ItemStack> itemsList = new ArrayList<>();
			ItemStack displayItem = XMaterial.BARRIER.parseItem();
			double cost = 0;

			final ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitName);
			for (final String param : kitSection.getKeys(false)) {

				if (param.equalsIgnoreCase("display-item")) {
					displayItem = XMaterial.matchXMaterial(kitSection.getString(param, "BARRIER").toUpperCase()).get().parseItem();

				} else if (param.equalsIgnoreCase("cost")) {
					cost = kitSection.getDouble(param, 0);

				} else if (param.equalsIgnoreCase("items")) {
					itemsList = loadItems(kitSection);
				}
			}
			final Kit kit = new Kit(kitName, kitName, displayItem, cost, itemsList);
			addKit(kit);
		}
	}

	private List<ItemStack> loadItems(final ConfigurationSection section) {
		final List<ItemStack> itemsList = new ArrayList<>();
		try {
			final ConfigurationSection itemsSection = section.getConfigurationSection("items");
			if (itemsSection != null && !itemsSection.getKeys(false).isEmpty()) {
				for (final String item : itemsSection.getKeys(false)) {
					final ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);
					final ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() != null ? XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() : XMaterial.STONE.parseItem();
					final int amount = itemSection.getInt("amount", 1);
					final ItemStack newI = ItemUtils.createItem(i.getType(), null, amount, null);
					newI.setDurability((short) itemSection.getInt("durability", newI.getDurability()));
					final ConfigurationSection enchantsSection = itemSection.getConfigurationSection("enchantments");
					if (enchantsSection != null) {
						for (final String enchant : enchantsSection.getKeys(false)) {
							final String enchantmentName = enchant.toUpperCase();
							final Enchantment e = XEnchantment.matchXEnchantment(enchantmentName).get().getEnchant();
							final int level = enchantsSection.getInt(enchantmentName);
							ItemUtils.addEnchant(newI, e, level);
						}
					}
					itemsList.add(newI);
				}
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("There is a problem with loading kit items!");
		}

		try {
			final ConfigurationSection potionsSection = section.getConfigurationSection("potions");
			if (potionsSection != null && !potionsSection.getKeys(false).isEmpty()) {
				for (final String potion : potionsSection.getKeys(false)) {
					final ConfigurationSection potionSection = potionsSection.getConfigurationSection(potion);
					final int amplifier = potionSection.getInt("amplifier", 1);
					final int amount = potionSection.getInt("amount", 1);
					final int duration = potionSection.getInt("duration", 44);
					final boolean splash = potionSection.getBoolean("splash", false);
					final PotionEffectType effectType = XPotion.matchXPotion(potion).get().getPotionEffectType();
					final ItemStack potionItem = ItemUtils.createPotionItem(effectType, "", amount, duration, amplifier, splash);

					itemsList.add(potionItem);
				}
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("There is a problem with loading kit potions!");
		}
		return itemsList;
	}

	public void addKit(final Kit kit) {
		this.kitsList.add(kit);
	}

	public Kit getKit(final String name) {
		for (final Kit kit : this.kitsList) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}
		return null;
	}

	public List<Kit> getKitsList() {
		return this.kitsList;
	}

	public void giveLobbyKit(final UHCPlayer p) {
		final ConfigurationSection section = config.getConfigurationSection("settings.inventories");
		if (section == null) return;
		for (final String selector : section.getKeys(false)) {
			if (!selector.matches("next|next-item|previous|previous-item|back|back-item|close|close-item")) {
				if (!section.getBoolean(selector + ".enabled")) return;
				final String displayName = section.getString(selector + ".display-name");
				final String material = section.getString(selector + ".display-item", "BARRIER").toUpperCase();
				final ItemStack item;
				if (material.contains("HEAD") || material.contains("SKULL_ITEM")) {
					item = Utils.getPlayerHead(p.getPlayer(), p.getName());
				} else {
					item = XMaterial.matchXMaterial(material).get().parseItem();
				}
				final int slot = section.getInt(selector + ".slot");
				final ItemStack newItem = ItemUtils.createItem(item.getType(), displayName, 1, null);
				if (slot < 0 || slot > 8) return;
				p.getPlayer().getInventory().setItem(slot, newItem);
			}
		}
	}

	public boolean exists(final String kitName) {
		for (final Kit kit : kitsList) {
			if (kit.getName().equalsIgnoreCase(kitName)) return true;
		}
		return false;
	}
}