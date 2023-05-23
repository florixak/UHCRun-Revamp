package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

    private GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null
                || event.getCurrentItem() == null
                || event.getCurrentItem().getType().equals(XMaterial.AIR.parseMaterial())) {
            return;
        }
        Player p = (Player) event.getWhoClicked();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        Inventory inv = event.getClickedInventory();

        if (event.getInventory().equals(p.getInventory())) {
            event.setCancelled(true);
            return;
        }

        if (inv.equals(gameManager.getGuiManager().getInventory("teams").getInventory())) {

            for (UHCTeam team : gameManager.getTeamManager().getTeams()) {
                event.setCancelled(true);
                p.closeInventory();

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(TextUtils.color(team.getDisplayName()))) {

                    uhcPlayer.setTeam(team);
                    uhcPlayer.sendMessage(Messages.TEAM_JOIN.toString()
                            .replace("%team%", TextUtils.color(team.getDisplayName())));
                }
            }
        }

        if (inv.equals(gameManager.getGuiManager().getInventory("kits").getInventory())) {

            for (Kit kit : gameManager.getKitsManager().getKits()) {
                event.setCancelled(true);
                p.closeInventory();

                if (event.getCurrentItem().getType().equals(kit.getDisplayItem())) {

                    uhcPlayer.setKit(kit);
                    uhcPlayer.sendMessage(Messages.KITS_SELECTED.toString()
                            .replace("%kit%", TextUtils.color(kit.getName())));
                }
            }
        }
    }
}