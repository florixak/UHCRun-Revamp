package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.gui.PaginatedMenu;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerksMenu extends PaginatedMenu {

    private final UHCPlayer uhcPlayer;
    private final List<Perk> perksList;

    public PerksMenu(MenuUtils menuUtils) {
        super(menuUtils, GameValues.INVENTORY.PERKS_TITLE);
        this.uhcPlayer = menuUtils.getUHCPlayer();
        this.perksList = GameManager.getGameManager().getPerksManager().getPerks();
    }

    @Override
    public int getSlots() {
        return GameValues.INVENTORY.PERKS_SLOTS;
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {
        if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
            close();
        } else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
            handlePaging(event, perksList);
        } else {
            handlePerkSelection(event);
        }

    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        ItemStack perkDisplayItem;

        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * currentPage + i;
            if (index >= perksList.size()) break;
            if (perksList.get(index) != null) {
                Perk perk = perksList.get(index);
                List<String> lore = new ArrayList<>();

                if (uhcPlayer.hasPerk() && uhcPlayer.getPerk().equals(perk)) {
                    lore.add(Messages.PERKS_INV_SELECTED.toString());
                } else {
                    if (!GameValues.PERKS.BOUGHT_FOREVER) {
                        lore.add(perk.getFormattedCost());
                    } else {
                        if (uhcPlayer.getData().hasPerkBought(perk) || perk.isFree()) {
                            lore.add(Messages.PERKS_INV_CLICK_TO_SELECT.toString());
                        } else {
                            lore.add(perk.getFormattedCost());
                        }
                    }
                }

                lore.addAll(perk.getDescription());

                perkDisplayItem = ItemUtils.createItem(perk.getDisplayItem().getType(), perk.getDisplayName(), 1, lore);

                inventory.addItem(perkDisplayItem);
            }
        }
    }

    @Override
    public void open() {
        if (!GameValues.KITS.ENABLED) {
            uhcPlayer.sendMessage(Messages.KITS_DISABLED.toString());
            return;
        }
        super.open();
    }

    private void handlePerkSelection(InventoryClickEvent event) {
        Perk selectedPerk = perksList.get(event.getSlot());
        close();

        if (!GameValues.PERKS.BOUGHT_FOREVER) {
            if (!selectedPerk.isFree() && uhcPlayer.getData().getMoney() < selectedPerk.getCost()) {
                uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
                return;
            }
            uhcPlayer.setPerk(selectedPerk);
            uhcPlayer.sendMessage(Messages.PERKS_MONEY_DEDUCT_INFO.toString());
        } else {
            if (uhcPlayer.getData().hasPerkBought(selectedPerk)) {
                uhcPlayer.setPerk(selectedPerk);
            } else {
                if (GameValues.INVENTORY.CONFIRM_PURCHASE_ENABLED) {
                    menuUtils.setSelectedPerkToBuy(selectedPerk);
                    new ConfirmPurchaseMenu(menuUtils).open();
                } else {
                    uhcPlayer.getData().buyPerk(selectedPerk);
                }
            }
        }
    }
}
