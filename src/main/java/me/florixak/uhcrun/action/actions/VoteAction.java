package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import org.bukkit.entity.Player;

public class VoteAction implements Action {
    @Override
    public String getIdentifier() {
        return "VOTE";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {


        player.sendMessage(Messages.VOTED.toString().replace("%voted%", InventoryListener.itemStack.getItemMeta().getDisplayName()));
    }
}
