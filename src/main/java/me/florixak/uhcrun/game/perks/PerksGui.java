package me.florixak.uhcrun.game.perks;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerksGui extends Gui {

    private final List<Perk> perks;

    public PerksGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 9 * 3, TextUtils.color(GameValues.INV_PERKS_TITLE));
        this.perks = gameManager.getPerksManager().getPerks();
    }

    @Override
    public void init() {
        super.init();
        ItemStack perk_item;

        for (int i = 0; i < perks.size(); i++) {
            Perk perk = perks.get(i);
            List<String> lore = new ArrayList<>();

            if (uhcPlayer.hasPerk() && uhcPlayer.getPerk().equals(perk)) {
                lore.add(TextUtils.color("&aSelected"));
            } else {
                lore.add(TextUtils.color("soon..."));
            }

            perk_item = this.createItem(XMaterial.matchXMaterial(perk.getDisplayItem()), perk.getName(), lore);

            getInventory().setItem(i, perk_item);
        }
    }

    @Override
    public void open() {
        if (!GameValues.PERKS_ENABLED) {
            uhcPlayer.sendMessage("Perks are disabled!");
            return;
        }
        super.open();
    }
}
