package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import me.florixak.uhcrun.manager.VoteManager;
import org.bukkit.entity.Player;

public class VoteAction implements Action {
    @Override
    public String getIdentifier() {
        return "VOTE";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {

        if (data.equals("doubled-hp")) {
            if (VoteManager.votedHP(player)) return;
            if (VoteManager.votedRanDrops(player)) VoteManager.ran_drops.remove(player.getUniqueId());
            if (VoteManager.votedNoPots(player)) VoteManager.no_pots.remove(player.getUniqueId());
            if (VoteManager.votedNoEnch(player)) VoteManager.no_ench.remove(player.getUniqueId());
            VoteManager.hp.add(player.getUniqueId());
        }
        if (data.equals("random-drops")) {
            if (VoteManager.votedRanDrops(player)) return;
            if (VoteManager.votedHP(player)) VoteManager.hp.remove(player.getUniqueId());
            if (VoteManager.votedNoPots(player)) VoteManager.no_pots.remove(player.getUniqueId());
            if (VoteManager.votedNoEnch(player)) VoteManager.no_ench.remove(player.getUniqueId());
            VoteManager.ran_drops.add(player.getUniqueId());
        }
        if (data.equals("no-enchants")) {
            if (VoteManager.votedNoEnch(player)) return;
            if (VoteManager.votedHP(player)) VoteManager.hp.remove(player.getUniqueId());
            if (VoteManager.votedNoPots(player)) VoteManager.no_pots.remove(player.getUniqueId());
            if (VoteManager.votedRanDrops(player)) VoteManager.ran_drops.remove(player.getUniqueId());
            VoteManager.no_ench.add(player.getUniqueId());
        }
        if (data.equals("no-potions")) {
            if (VoteManager.votedNoPots(player)) return;
            if (VoteManager.votedHP(player)) VoteManager.hp.remove(player.getUniqueId());
            if (VoteManager.votedRanDrops(player)) VoteManager.ran_drops.remove(player.getUniqueId());
            if (VoteManager.votedNoEnch(player)) VoteManager.no_ench.remove(player.getUniqueId());
            VoteManager.no_pots.add(player.getUniqueId());
        }
        player.sendMessage(Messages.VOTED.toString().replace("%voted%", InventoryListener.itemStack.getItemMeta().getDisplayName()));
    }
}
