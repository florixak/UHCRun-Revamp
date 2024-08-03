package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.text.TextUtils;

import java.util.List;

public class PerkBonus {

	private final List<Double> coins;
	private final List<Double> uhcExp;
	private final List<Integer> exp;

	public PerkBonus(final List<Double> coins, final List<Double> uhcExp, final List<Integer> exp) {
		this.coins = coins;
		this.uhcExp = uhcExp;
		this.exp = exp;
	}

	public List<Double> getCoins() {
		return coins;
	}

	public String getFormattedCoins() {
		if (getCoins().isEmpty()) return "0";
		if (getCoins().size() == 1) return String.valueOf(getCoins().get(0));
		return getCoins().get(0) + "-" + getCoins().get(1);
	}

	public List<Double> getUhcExp() {
		return uhcExp;
	}

	public String getFormattedUhcExp() {
		if (getUhcExp().isEmpty()) return "0";
		if (getUhcExp().size() == 1) return String.valueOf(getUhcExp().get(0));
		return getUhcExp().get(0) + "-" + getUhcExp().get(1);
	}

	public List<Integer> getExp() {
		return exp;
	}

	public String getFormattedExp() {
		if (getExp().isEmpty()) return "0";
		if (getExp().size() == 1) return String.valueOf(getExp().get(0));
		return getExp().get(0) + "-" + getExp().get(1);
	}

	public void giveBonus(final UHCPlayer uhcPlayer) {
		final double randomCoins = getCoins().get(0) + (getCoins().get(1) - getCoins().get(0)) * MathUtils.getRandom().nextDouble();
		final double randomUHCExp = getUhcExp().get(0) + (getUhcExp().get(1) - getUhcExp().get(0)) * MathUtils.getRandom().nextDouble();
		final int randomExp = MathUtils.randomInteger(getExp().get(0), getExp().get(1));

		uhcPlayer.getData().depositMoney(randomCoins);
		uhcPlayer.getData().addUHCExp(randomUHCExp);
		uhcPlayer.giveExp(randomExp);
		uhcPlayer.sendMessage(Messages.PERKS_BONUS_RECEIVED.toString().replace("%coins%", TextUtils.formatToOneDecimal(randomCoins)).replace("%uhcExp%", TextUtils.formatToOneDecimal(randomUHCExp)).replace("%exp%", String.valueOf(randomExp)));
	}

}
