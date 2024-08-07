package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PerkItem {

	private final String displayName;
	private final ItemStack item;
	private final List<Integer> amount;
	private final int chance;

	public PerkItem(final ItemStack item, final List<Integer> amount, final int chance) {
		this.item = item;
		this.amount = amount;
		this.chance = chance;
		this.displayName = TextUtils.toNormalCamelText(item.getType().toString());
	}

	public ItemStack getItem() {
		return item;
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<Integer> getAmount() {
		return amount;
	}

	public String getFormattedAmount() {
		if (amount.isEmpty()) return "0";
		if (amount.size() == 1) return String.valueOf(amount.get(0));
		return amount.get(0) + "-" + amount.get(1);
	}

	public int getChance() {
		return chance;
	}

	public void giveItem(final UHCPlayer uhcPlayer) {
		final int num = MathUtils.randomInteger(1, 100);
		if (num > chance) return;
		uhcPlayer.sendMessage(Messages.PERKS_ITEM_RECEIVED.toString().replace("%item%", getDisplayName()));
		uhcPlayer.getPlayer().getInventory().addItem(item);
	}
}
