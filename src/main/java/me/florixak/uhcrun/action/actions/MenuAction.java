package me.florixak.uhcrun.action.actions;


import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.inventory.AbstractInventory;
import me.florixak.uhcrun.manager.SoundManager;
import org.bukkit.entity.Player;

public class MenuAction implements Action {

    @Override
    public String getIdentifier() {
        return "MENU";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {
        AbstractInventory inventory = plugin.getInventoryManager().getInventory(data);

        if (inventory != null) {
            inventory.openInventory(player);
            UHCRun.plugin.getSoundManager().playInvOpenSound(player);
        } else {
            plugin.getLogger().warning("[MENU] Action Failed: Menu '" + data + "' not found.");
        }
    }
}
