package me.florixak.uhcrevamp.game.kits;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {

	private final String name;
	private final String displayName;
	private final List<ItemStack> items;
	private final ItemStack displayItem;
	private final double cost;

	public Kit(final String name, final String displayName, final ItemStack displayItem, final double cost, final List<ItemStack> items) {
		this.name = name;
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.cost = cost;
		this.items = items;
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return TextUtils.color(this.displayName);
	}

	public ItemStack getDisplayItem() {
		return this.displayItem;
	}

	public double getCost() {
		return this.cost;
	}

	public boolean isFree() {
		return getCost() == 0;
	}

	public List<ItemStack> getItems() {
		return this.items;
	}

	public String getFormattedCost() {
		return Messages.KITS_COST.toString().replace("%cost%", String.valueOf(getCost()));
	}

	public void giveKit(final UHCPlayer uhcPlayer) {
		final Player p = uhcPlayer.getPlayer();

		for (final ItemStack item : getItems()) {
			p.getInventory().addItem(item);
		}
	}
}
