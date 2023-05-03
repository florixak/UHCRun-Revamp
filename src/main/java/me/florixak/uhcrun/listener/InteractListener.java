package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private GameManager gameManager;
    private FileConfiguration config;

    public InteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {

        Player p = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null) return;

        if (!gameManager.isPlaying() && event.getClickedInventory().equals(p.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (gameManager.getGameState() == GameState.LOBBY || gameManager.getGameState() == GameState.STARTING) {
            if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR || item.getItemMeta().getDisplayName() == null) return;

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {

                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(config.getString("settings.selectors.teams.display-name")))) {
                    gameManager.getGuiManager().getGui("teams").openInv(p);
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(config.getString("settings.selectors.kits.display-name")))) {
                    gameManager.getGuiManager().getGui("kits").openInv(p);
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(config.getString("settings.selectors.perks.display-name")))) {
                    gameManager.getGuiManager().getGui("perks").openInv(p);
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(config.getString("settings.selectors.statistics.display-name")))) {
                    gameManager.getGuiManager().getGui("statistics").openInv(p);

                }
            }
        }
    }
}