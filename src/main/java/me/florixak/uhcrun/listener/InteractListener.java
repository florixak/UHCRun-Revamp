package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.kits.KitsGui;
import me.florixak.uhcrun.game.perks.PerksGui;
import me.florixak.uhcrun.game.statistics.StatisticsGui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.teams.TeamGui;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private final GameManager gameManager;
    private final FileConfiguration config;

    public InteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
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
                        TextUtils.color(GameValues.INV_TEAMS_TITLE))) {
                    new TeamGui(gameManager, uhcPlayer).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INV_KITS_TITLE))) {
                    // gameManager.getGuiManager().getInventory("kits").openInv(p);
                    new KitsGui(gameManager, uhcPlayer).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INV_PERKS_TITLE))) {
                    new PerksGui(gameManager, uhcPlayer).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INV_STATS_TITLE))) {
                    new StatisticsGui(gameManager, uhcPlayer).open();
                }
            }
        }
    }
}