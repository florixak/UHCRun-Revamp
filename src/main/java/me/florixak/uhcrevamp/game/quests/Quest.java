package me.florixak.uhcrevamp.game.quests;

import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;

import java.util.List;

public class Quest {

	private final String id;
	private final QuestType questType;
	private final String displayName;
	private final String displayItem;
	private final List<String> description;
	private final QuestReward questReward;

	public Quest(final String id, final String displayName, final String displayItem, final QuestType questType, final List<String> description, final QuestReward questReward) {
		this.id = id;
		this.questType = questType;
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.description = description;
		this.questReward = questReward;
	}

	public String getId() {
		return id;
	}

	public QuestType getQuestType() {
		return questType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDisplayItem() {
		return displayItem;
	}

	public Material parseDisplayItem() {
		return XMaterial.matchXMaterial(displayItem).get().parseMaterial();
	}

	public List<String> getDescription() {
		return description;
	}

	public QuestReward getReward() {
		return questReward;
	}

	public void giveReward(final UHCPlayer uhcPlayer) {
		questReward.giveReward(uhcPlayer);
	}


	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Quest && ((Quest) obj).getId().equals(id);
	}
}
