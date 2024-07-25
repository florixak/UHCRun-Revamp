package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;

public class ConfirmGui extends Gui {

    public ConfirmGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 3 * 9, TextUtils.color(GameValues.INVENTORY.CONFIRM_PURCHASE_TITLE));
    }

    @Override
    public void init() {
        super.init();
        setItem(11, ItemUtils.createItem(XMaterial.matchXMaterial(GameValues.INVENTORY.CONFIRM_PURCHASE_ITEM).get().parseMaterial(), TextUtils.color(GameValues.INVENTORY.CONFIRM_PURCHASE_NAME), 1, null));
        setItem(15, ItemUtils.createItem(XMaterial.matchXMaterial(GameValues.INVENTORY.CANCEL_PURCHASE_ITEM).get().parseMaterial(), TextUtils.color(GameValues.INVENTORY.CANCEL_PURCHASE_NAME), 1, null));
    }

    @Override
    public void open() {
        super.open();
    }
}
