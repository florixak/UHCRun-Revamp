package me.florixak.uhcrevamp.game.quests;

import me.florixak.uhcrevamp.game.player.UHCPlayer;

public class QuestReward {

	private final double money;
	private final double uhcExp;

	public QuestReward(final double money, final double uhcExp) {
		this.money = money;
		this.uhcExp = uhcExp;
	}

	public double getMoney() {
		return money;
	}

	public double getUhcExp() {
		return uhcExp;
	}

	public void giveReward(final UHCPlayer uhcPlayer) {
		if (money > 0) {
			uhcPlayer.getData().depositMoney(money);
		}
		if (uhcExp > 0) {
			uhcPlayer.getData().addUHCExp(uhcExp);
		}
	}
}
