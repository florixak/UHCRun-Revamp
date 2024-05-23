package me.florixak.uhcrun.game.perks;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.manager.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerksGui extends Gui {

    public PerksGui(GameManager gameManager) {
        super(gameManager, 9 * 3, "Perks");
    }

    @Override
    public void init() {
        super.init();
        ItemStack perk_item;
        List<Perk> perks = gameManager.getPerksManager().getPerks();
        Player p = getWhoOpen();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

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
    public void openInv(Player p) {
        if (!GameValues.PERKS_ENABLED) {
            p.sendMessage("Perks are disabled!");
            return;
        }
        super.openInv(p);
    }
}
