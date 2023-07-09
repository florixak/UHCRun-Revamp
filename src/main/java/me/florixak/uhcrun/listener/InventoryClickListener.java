package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null || isNull(event.getCurrentItem())) {
            return;
        }

        Player p = (Player) event.getWhoClicked();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        Inventory inv = event.getClickedInventory();

        if (!gameManager.isPlaying()) {
            event.setCancelled(true);
        }

        if (inv.equals(gameManager.getGuiManager().getInventory("teams").getInventory())) {
            event.setCancelled(true);

            if (gameManager.isPlaying()) return;

            for (UHCTeam team : gameManager.getTeamManager().getTeams()) {

                if (gameManager.getTeamManager().getTeams().get(event.getSlot()) == team) {
                    p.closeInventory();
                    team.join(uhcPlayer);
                    uhcPlayer.sendMessage(Messages.TEAM_JOIN.toString()
                            .replace("%team%", TextUtils.color(team.getDisplayName())));
                }
            }
        }

        if (inv.equals(gameManager.getGuiManager().getInventory("kits").getInventory())) {
            event.setCancelled(true);

            if (gameManager.isPlaying()) return;

            for (Kit kit : gameManager.getKitsManager().getKits()) {

                if (gameManager.getKitsManager().getKits().get(event.getSlot()) == kit) {
                    p.closeInventory();
                    uhcPlayer.setKit(kit);
                }
            }
        }
    }

    private boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
    }
}