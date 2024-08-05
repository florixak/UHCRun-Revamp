package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.game.quests.Quest;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.gui.PaginatedMenu;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QuestsMenu extends PaginatedMenu {

	private final UHCPlayer uhcPlayer;
	private final GameManager gameManager = GameManager.getGameManager();
	private final List<Quest> quests = gameManager.getQuestManager().getQuests();

	public QuestsMenu(final MenuUtils menuUtils) {
		super(menuUtils, "Quests");
		this.uhcPlayer = menuUtils.getUHCPlayer();
	}

	@Override
	public int getSlots() {
		return GameValues.INVENTORY.QUESTS_SLOTS;
	}

	@Override
	public int getItemsCount() {
		return quests.size();
	}

	@Override
	public void handleMenuClicks(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
			close();
		} else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
			handlePaging(event, quests);
		}
	}

	@Override
	public void setMenuItems() {
		addMenuBorder();
		ItemStack questDisplayItem;

		for (int i = getStartIndex(); i < getEndIndex(); i++) {
			final Quest quest = quests.get(i);
			final List<String> lore = new ArrayList<>();
			final boolean completed = uhcPlayer.getQuestData().isCompletedQuest(quest.getId());
			final int count = quest.getQuestType().getCount();

			for (final String description : quest.getDescription()) {
				lore.add(TextUtils.color(description
						.replace("%count%", String.valueOf(count))
						.replace("%progress%", String.valueOf(!completed ? uhcPlayer.getQuestData().getProgress(quest.getId()) : count))
						.replace("%material%", quest.getQuestType().hasMaterial() ?
								TextUtils.toNormalCamelText(quest.getQuestType().parseMaterial().name()) :
								"None")
						.replace("%money%", String.valueOf(quest.getReward().getMoney()))
						.replace("%uhc-exp%", String.valueOf(quest.getReward().getUhcExp()))
						.replace("%currency%", Messages.CURRENCY.toString())
						.replace("%status%", completed ? "&aCompleted" : "&cNot Completed"
						)));
			}

			questDisplayItem = ItemUtils.createItem(quest.parseDisplayItem(), quest.getDisplayName(), 1, lore);

			if (completed) {
				ItemUtils.addGlow(questDisplayItem);
			}

			inventory.setItem(i - getStartIndex(), questDisplayItem);

		}
	}
}
