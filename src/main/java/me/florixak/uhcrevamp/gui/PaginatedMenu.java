package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

	protected int currentPage = 0;

	protected String title = "Inventory";
	protected int slots = 54;

	protected int index = 0;

	public PaginatedMenu(final MenuUtils menuUtils, final String title) {
		super(menuUtils);
		this.title = title;
	}

	@Override
	public String getMenuName() {
		return formattedMenuName();
	}

	private String formattedMenuName() {
		if (getMaxPages() == 1) {
			return TextUtils.color(title);
		} else {
			return TextUtils.color(title + " - " + (currentPage + 1) + " / " + getMaxPages());
		}
	}

	public int getMaxPages() {
		return (int) Math.ceil((double) getItemsCount() / getMaxItemsPerPage());
	}

	public abstract int getItemsCount();

	@Override
	public int getSlots() {
		return slots;
	}

	public int getStartIndex() {
		return currentPage * getMaxItemsPerPage();
	}

	public int getEndIndex() {
		return Math.min(getStartIndex() + getMaxItemsPerPage(), getItemsCount());
	}

	public void addMenuBorder() {
		if (currentPage > 0)
			inventory.setItem(getSlots() - 6,
					ItemUtils.createItem(XMaterial.matchXMaterial(GameValues.INVENTORY.PREVIOUS_ITEM).get().parseMaterial(),
							TextUtils.color(GameValues.INVENTORY.PREVIOUS_TITLE), 1, null));

		inventory.setItem(getSlots() - 5,
				ItemUtils.createItem(XMaterial.matchXMaterial(GameValues.INVENTORY.CLOSE_ITEM).get().parseMaterial(),
						TextUtils.color(GameValues.INVENTORY.CLOSE_TITLE), 1, null));

		if (currentPage < getMaxPages() - 1)
			inventory.setItem(getSlots() - 4,
					ItemUtils.createItem(XMaterial.matchXMaterial(GameValues.INVENTORY.NEXT_ITEM).get().parseMaterial(),
							TextUtils.color(GameValues.INVENTORY.NEXT_TITLE), 1, null));
	}

	private int getMaxItemsPerPage() {
		return getSlots() - 9;
	}

	public void handlePaging(final InventoryClickEvent event, final List<?> recipesList) {
		if (event.getCurrentItem().getItemMeta() == null) return;
		if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.PREVIOUS_TITLE))) {
			handlePrevious();
		} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.NEXT_TITLE))) {
			handleNext(recipesList);
		}
	}

	private void handlePrevious() {
		if (currentPage == 0) {
			menuUtils.getUHCPlayer().sendMessage(ChatColor.GRAY + "You are already on the first page.");
		} else {
			currentPage -= 1;
			super.open();
		}
	}

	private void handleNext(final List<?> list) {
		if (!((index + 1) >= list.size())) {
			currentPage += 1;
			super.open();
		} else {
			menuUtils.getUHCPlayer().sendMessage(ChatColor.GRAY + "You are on the last page.");
		}
	}
}
