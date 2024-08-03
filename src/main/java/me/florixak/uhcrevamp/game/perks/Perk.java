package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Perk {

	private final String name;
	private final ItemStack displayItem;
	private final double cost;
	private final List<String> description;
	private final List<PerkBonus> perkBonus;
	private final List<PerkEffect> perkEffect;
	private final List<PerkItem> perkItem;

	public Perk(final String name, final ItemStack displayItem, final double cost, final List<String> description, final List<PerkBonus> perkBonus, final List<PerkEffect> perkEffect, final List<PerkItem> perkItem) {
		this.name = name;
		this.displayItem = displayItem;
		this.cost = cost;
		this.description = description;
		this.perkBonus = perkBonus;
		this.perkEffect = perkEffect;
		this.perkItem = perkItem;
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return TextUtils.color(this.name);
	}

	public ItemStack getDisplayItem() {
		return this.displayItem;
	}

	public double getCost() {
		return this.cost;
	}

	public String getFormattedCost() {
		return Messages.PERKS_COST.toString().replace("%cost%", String.valueOf(getCost()));
	}

	public boolean isFree() {
		return getCost() == 0;
	}

	public List<String> getDescription() {
		final List<String> formattedDescription = new ArrayList<>();
		for (String desc : description) {
			if (hasPerkEffect()) {
				for (final PerkEffect effect : perkEffect) {
					desc = desc
							.replace("%effect%", effect.getEffectName())
							.replace("%effect-level%", String.valueOf(effect.getFormattedAmplifier()))
							.replace("%effect-duration%", String.valueOf(effect.getDuration()));
				}
			}
			if (hasPerkItem()) {
				for (final PerkItem item : perkItem) {
					desc = desc
							.replace("%item-name%", TextUtils.toNormalCamelText(item.getItem().getType().toString()))
							.replace("%item-amount%", String.valueOf(item.getFormattedAmount()))
							.replace("%item-chance%", item.getChance() + "%");
				}
			}
			if (hasPerkBonus()) {
				for (final PerkBonus bonus : perkBonus) {
					desc = desc
							.replace("%bonus-coins%", bonus.getFormattedCoins())
							.replace("%bonus-uhc-exp%", bonus.getFormattedUhcExp())
							.replace("%bonus-exp%", bonus.getFormattedExp())
							.replace("%currency%", Messages.CURRENCY.toString());
				}
			}
			formattedDescription.add(TextUtils.color(desc));
		}
		return formattedDescription;
	}

	public List<PerkBonus> getPerkBonuses() {
		return this.perkBonus;
	}

	public List<PerkEffect> getPerkEffects() {
		return this.perkEffect;
	}

	public List<PerkItem> getPerkItems() {
		return this.perkItem;
	}

	public boolean hasPerkBonus() {
		return perkBonus != null && !perkBonus.isEmpty();
	}

	public boolean hasPerkEffect() {
		return perkEffect != null && !perkEffect.isEmpty();
	}

	public boolean hasPerkItem() {
		return perkEffect != null && !perkItem.isEmpty();
	}

	public void givePerk(final UHCPlayer uhcPlayer) {
		if (hasPerkEffect()) {
			for (final PerkEffect effect : perkEffect) {
				effect.giveEffect(uhcPlayer);
			}
		}
		if (hasPerkItem()) {
			for (final PerkItem item : perkItem) {
				item.giveItem(uhcPlayer);
			}
		}
		if (hasPerkBonus()) {
			for (final PerkBonus bonus : perkBonus) {
				bonus.giveBonus(uhcPlayer);
			}
		}
	}

}