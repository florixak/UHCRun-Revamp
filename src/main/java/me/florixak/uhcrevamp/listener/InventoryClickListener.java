package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.gui.Menu;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

	private final GameManager gameManager;

	public InventoryClickListener(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void handleInventoryClick(final InventoryClickEvent event) {
		if (event.getClickedInventory() == null || isNull(event.getCurrentItem()) || event.getClickedInventory() instanceof AnvilInventory)
			return;

		if (!gameManager.isPlaying() || gameManager.isEnding()) {
			event.setCancelled(true);
		}

		final InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Menu) {
			event.setCancelled(true);
			if (event.getCurrentItem() == null) {
				return;
			}
			final Menu menu = (Menu) holder;
			menu.handleMenuClicks(event);
		}
	}

	private boolean isNull(final ItemStack item) {
		return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
	}
}