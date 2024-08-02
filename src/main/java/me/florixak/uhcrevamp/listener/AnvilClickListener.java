package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (!(event.getInventory() instanceof AnvilInventory)) return;

		AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();

		ItemStack leftItem = anvilInventory.getItem(0);
		ItemStack rightItem = anvilInventory.getItem(1);
		String newName = anvilInventory.getRenameText();

		ItemStack combinedItem = combineItems(leftItem, rightItem, newName);
		if (combinedItem != null) {
			event.setCurrentItem(combinedItem);
		}

	}

	private ItemStack combineItems(ItemStack item1, ItemStack item2, String newName) {
		try {
			ItemStack combinedItem = new ItemStack(item1.getType());

			// Combine the durability of both items
			int newDurability = item1.getDurability() + item2.getDurability();
			combinedItem.setDurability((short) newDurability);

			// Combine enchantments
			combinedItem.addUnsafeEnchantments(item1.getEnchantments());
			combinedItem.addUnsafeEnchantments(item2.getEnchantments());

			// Handle custom names
			ItemMeta meta = combinedItem.getItemMeta();
			if (meta != null) {
				if (newName != null && !newName.isEmpty()) {
					meta.setDisplayName(newName);
				} else {
					if (item1.hasItemMeta() && item1.getItemMeta().hasDisplayName()) {
						meta.setDisplayName(item1.getItemMeta().getDisplayName());
					} else if (item2.hasItemMeta() && item2.getItemMeta().hasDisplayName()) {
						meta.setDisplayName(item2.getItemMeta().getDisplayName());
					}
				}
				combinedItem.setItemMeta(meta);
			}

			// Apply enchantments from books
			if (item1.getType() == Material.ENCHANTED_BOOK || item2.getType() == Material.ENCHANTED_BOOK) {
				if (item1.getType() == Material.ENCHANTED_BOOK) {
					combinedItem.addUnsafeEnchantments(((EnchantmentStorageMeta) item1.getItemMeta()).getStoredEnchants());
				}
				if (item2.getType() == Material.ENCHANTED_BOOK) {
					combinedItem.addUnsafeEnchantments(((EnchantmentStorageMeta) item2.getItemMeta()).getStoredEnchants());
				}
			}

			return combinedItem;
		} catch (Exception ignore) {
			return XMaterial.matchXMaterial(Material.BARRIER).parseItem();
		}
	}
}
