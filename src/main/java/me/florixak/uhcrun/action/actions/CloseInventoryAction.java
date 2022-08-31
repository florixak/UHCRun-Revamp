package me.florixak.uhcrun.action.actions;


import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import org.bukkit.entity.Player;

public class CloseInventoryAction implements Action {

    @Override
    public String getIdentifier() {
        return "CLOSE";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {
        player.closeInventory();
    }
}
