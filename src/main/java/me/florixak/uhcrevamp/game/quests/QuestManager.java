package me.florixak.uhcrevamp.game.quests;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
	private final FileConfiguration questConfig;
	private final List<Quest> quests = new ArrayList<>();

	public QuestManager(final GameManager gameManager) {
		this.questConfig = gameManager.getConfigManager().getFile(ConfigType.QUESTS).getConfig();
	}

	public void load() {
		loadQuests();
	}

	private void loadQuests() {
		for (final String key : questConfig.getConfigurationSection("quests").getKeys(false)) {
			final String name = questConfig.getString("quests." + key + ".display-name", "Quest " + key);
			final String displayItem = questConfig.getString("quests." + key + ".display-item", "BOOK");
			final List<String> description = questConfig.getStringList("quests." + key + ".description");
			final String type = questConfig.getString("quests." + key + ".type");
			final int count = questConfig.getInt("quests." + key + ".count");
			final double money = questConfig.getDouble("quests." + key + ".reward.money");
			final double uhcExp = questConfig.getDouble("quests." + key + ".reward.uhc-exp");
			if (questConfig.contains("quests." + key + ".material")) {
				final String material = questConfig.getString("quests." + key + ".material");
				quests.add(new Quest(key, name, displayItem, new QuestType(type, count, material), description, new QuestReward(money, uhcExp)));
				continue;
			}
			quests.add(new Quest(key, name, displayItem, new QuestType(type, count), description, new QuestReward(money, uhcExp)));
		}
	}

	public Quest getQuest(final String questId) {
		for (final Quest quest : quests) {
			if (quest.getId().equals(questId)) {
				return quest;
			}
		}
		return null;
	}

	public List<Quest> getQuests() {
		return quests;
	}

//	public void resetDailyQuests() {
//		dailyQuests.clear();
//		saveQuests();
//	}

	public void onDisable() {
		this.quests.clear();
	}
}