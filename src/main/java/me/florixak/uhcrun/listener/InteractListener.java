package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.gui.KitsGui;
import me.florixak.uhcrun.game.gui.PerksGui;
import me.florixak.uhcrun.game.gui.StatisticsGui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.teams.TeamGui;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private final GameManager gameManager;

    public InteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        ItemStack item = p.getInventory().getItemInMainHand();

        if (gameManager.getGameState() == GameState.LOBBY || gameManager.getGameState() == GameState.STARTING) {
            if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR || item.getItemMeta().getDisplayName() == null) return;

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {

                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.TEAMS_TITLE))) {
                    new TeamGui(gameManager, uhcPlayer).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.KITS_TITLE))) {
                    // gameManager.getGuiManager().getInventory("kits").openInv(p);
                    new KitsGui(gameManager, uhcPlayer).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.PERKS_TITLE))) {
                    new PerksGui(gameManager, uhcPlayer).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.STATS_TITLE))) {
                    new StatisticsGui(gameManager, uhcPlayer).open();
                }
            }
        }
    }
}