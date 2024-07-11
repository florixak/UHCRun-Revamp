package me.florixak.uhcrun.game.gui;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.perks.Perk;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerksGui extends Gui {

    private final List<Perk> perks;

    public PerksGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 3 * GameValues.COLUMNS, TextUtils.color(GameValues.INVENTORY.PERKS_TITLE));
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

            perk_item = this.createItem(perk.getDisplayItem(), perk.getName(), lore);

            getInventory().setItem(i, perk_item);
        }
    }

    @Override
    public void open() {
        if (!GameValues.PERKS.ENABLED) {
            uhcPlayer.sendMessage("Perks are disabled!");
            return;
        }
        super.open();
    }
}
