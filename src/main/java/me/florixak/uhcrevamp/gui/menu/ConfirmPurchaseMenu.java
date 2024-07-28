package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.kits.Kit;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.Menu;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConfirmPurchaseMenu extends Menu {

    private final UHCPlayer uhcPlayer;
    private final Kit kitToBuy;
    private final Perk perkToBuy;
    private double moneyToWithdraw = 0;

    public ConfirmPurchaseMenu(MenuUtils menuUtils) {
        super(menuUtils);
        this.uhcPlayer = menuUtils.getUHCPlayer();
        this.kitToBuy = menuUtils.getSelectedKitToBuy();
        this.perkToBuy = menuUtils.getSelectedPerkToBuy();
    }

    @Override
    public String getMenuName() {
        return TextUtils.color("&6Are you sure?");
    }

    @Override
    public int getSlots() {
        return GameValues.INVENTORY.CONFIRM_PURCHASE_SLOTS;
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {

        uhcPlayer.getPlayer().closeInventory();

        if (event.getSlot() == 11) {
            if (kitToBuy != null) {
                uhcPlayer.getData().buyKit(menuUtils.getSelectedKitToBuy());
                menuUtils.setSelectedKitToBuy(null);
            } else if (perkToBuy != null) {
                uhcPlayer.getData().buyPerk(menuUtils.getSelectedPerkToBuy());
                menuUtils.setSelectedPerkToBuy(null);
            } else {
                uhcPlayer.getData().withdrawMoney(0);
            }
        } else if (event.getSlot() == 15) {
            uhcPlayer.sendMessage(Messages.CANCELLED_PURCHASE.toString());
        }
    }

    @Override
    public void setMenuItems() {
        moneyToWithdraw = kitToBuy != null ? kitToBuy.getCost() : perkToBuy.getCost();

        inventory.setItem(11, ItemUtils.createItem(
                XMaterial.matchXMaterial(GameValues.INVENTORY.CONFIRM_PURCHASE_ITEM).get().parseMaterial(),
                TextUtils.color(GameValues.INVENTORY.CONFIRM_PURCHASE_NAME
                        .replace("%cost%", String.valueOf(moneyToWithdraw))
                        .replace("%currency%", Messages.CURRENCY.toString())),
                1,
                null)
        );
        inventory.setItem(15, ItemUtils.createItem(
                XMaterial.matchXMaterial(GameValues.INVENTORY.CANCEL_PURCHASE_ITEM).get().parseMaterial(),
                TextUtils.color(GameValues.INVENTORY.CANCEL_PURCHASE_NAME),
                1,
                null)
        );
    }

    @Override
    public void open() {
        super.open();
    }
}
