package me.florixak.uhcrevamp.game.quests;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PlayerQuestData {

	private final GameManager gameManager = GameManager.getGameManager();
	private final UHCPlayer uhcPlayer;
	private final FileConfiguration questConfig;
	private final Set<String> completedQuests = new HashSet<>();
	private final Map<String, Integer> questProgress = new HashMap<>();

	public PlayerQuestData(final UHCPlayer uhcPlayer) {
		this.uhcPlayer = uhcPlayer;
		this.questConfig = gameManager.getConfigManager().getFile(ConfigType.QUESTS).getConfig();

		initializeData();
	}

	private void initializeData() {
		if (!hasData()) return;
		loadData();
	}

	private boolean hasData() {
		return questConfig.contains("players." + uhcPlayer.getUUID().toString());
	}

	private void loadData() {
		if (questConfig.getConfigurationSection("players") == null)
			return;

		final String uuid = uhcPlayer.getUUID().toString();

		if (questConfig.getConfigurationSection("players." + uuid) == null)
			return;

		for (final String questId : questConfig.getStringList("players." + uuid + ".completed")) {
			addCompletedQuest(questId);
		}
		for (final String questId : questConfig.getConfigurationSection("players." + uuid + ".progress").getKeys(false)) {
			if (completedQuests.contains(questId)) {
				continue;
			}
			setProgress(questId, questConfig.getInt("players." + uuid + ".progress." + questId));
		}

	}

	public void savePlayerQuestData() {
		final String path = "players." + uhcPlayer.getUUID().toString();
		questConfig.set(path + ".completed", new ArrayList<>(completedQuests));
		questProgress.forEach((key, value) -> questConfig.set(path + ".progress." + key,
				getProgress(key) == gameManager.getQuestManager().getQuest(key).getQuestType().getCount() ? null : value));
		gameManager.getConfigManager().saveFile(ConfigType.QUESTS);
	}

	public Set<String> getCompletedQuests() {
		return completedQuests;
	}

	public Map<String, Integer> getQuestProgress() {
		return questProgress;
	}

	private void addCompletedQuest(final String questId) {
		completedQuests.add(questId);
	}

	private void setProgress(final String questId, final int progress) {
		questProgress.put(questId, progress);
	}

	public void addProgressToTypes(final String type, final Material material) {
		for (final Quest quest : gameManager.getQuestManager().getQuests()) {
			if (isCompletedQuest(quest.getId())) continue;
			final QuestType questType = quest.getQuestType();
			if (questType.getType().equalsIgnoreCase(type)) {
				if (questType.hasMaterial()
						&& material != null
						&& questType.parseMaterial().equals(material)) {
					addProgress(quest.getId());
				} else if (!questType.hasMaterial()) {
					addProgress(quest.getId());
				}
			}
		}
	}

	private void addProgress(final String questId) {
		if (isCompletedQuest(questId)) {
			return;
		}
		if (!questProgress.containsKey(questId)) {
			questProgress.put(questId, 0);
			return;
		}
		questProgress.put(questId, getProgress(questId) + 1);

		if (getProgress(questId) >= gameManager.getQuestManager().getQuest(questId).getQuestType().getCount()) {
			completeQuest(gameManager.getQuestManager().getQuest(questId));
		}
	}

	public int getProgress(final String questId) {
		if (!questProgress.containsKey(questId)) {
			questProgress.put(questId, 0);
		}
		return questProgress.getOrDefault(questId, 0);
	}

	private void completeQuest(final Quest quest) {
		addCompletedQuest(quest.getId());
		questProgress.remove(quest.getId());
		quest.giveReward(uhcPlayer);
		uhcPlayer.sendMessage(Messages.QUEST_COMPLETED.toString().replace("%quest%", quest.getDisplayName()));
		uhcPlayer.sendMessage(Messages.QUEST_REWARD.toString()
				.replace("%quest%", quest.getDisplayName())
				.replace("%money%", String.valueOf(quest.getReward().getMoney()))
				.replace("%uhc-exp%", String.valueOf(quest.getReward().getUhcExp()))
				.replace("%currency%", Messages.CURRENCY.toString()));
		gameManager.getSoundManager().playQuestCompletedSound(uhcPlayer.getPlayer());
	}

	public boolean hasQuestWithTypeOf(final String questType) {
		for (final Quest quest : gameManager.getQuestManager().getQuests()) {
			if (!isCompletedQuest(quest.getId())
					&& quest.getQuestType().getType().equalsIgnoreCase(questType)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCompletedQuest(final String questId) {
		return completedQuests.contains(questId);
	}
}