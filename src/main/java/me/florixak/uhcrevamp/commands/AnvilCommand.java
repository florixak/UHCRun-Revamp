package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import static me.florixak.uhcrevamp.game.Permissions.ANVIL;

public class AnvilCommand implements CommandExecutor, Listener {

	private final UHCRevamp plugin;

	public AnvilCommand(UHCRevamp plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player p = (Player) sender;

		if (!p.hasPermission(ANVIL.getPerm())) {
			p.sendMessage(Messages.NO_PERM.toString());
			return true;
		}

		Inventory inventory = Bukkit.createInventory(p, InventoryType.ANVIL, "Anvil");
		p.openInventory(inventory);

		return true;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getInventory() instanceof AnvilInventory)) return;

		AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
		if (event.getRawSlot() != 2) return; // Only handle the output slot

		ItemStack leftItem = anvilInventory.getItem(0);
		ItemStack rightItem = anvilInventory.getItem(1);
		String newName = anvilInventory.getRenameText();

		if (leftItem == null || rightItem == null) return;

		ItemStack combinedItem = combineItems(leftItem, rightItem, newName);
		if (combinedItem != null) {
			event.setCurrentItem(combinedItem);
		}
	}

	private boolean canCombine(ItemStack item1, ItemStack item2) {
		if (item1 == null || item2 == null) return false;
		if (!item1.getType().equals(item2.getType())) return false;
		return true;
	}

	private ItemStack combineItems(ItemStack item1, ItemStack item2, String newName) {
		if (!canCombine(item1, item2)) return null;

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
	}
}