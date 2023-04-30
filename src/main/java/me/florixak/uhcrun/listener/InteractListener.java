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
        if (!gameManager.isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleporterClick(InventoryClickEvent event) {

        Player p = (Player) event.getWhoClicked();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        if (!uhcPlayer.isDead()) return;

        if (event.getClickedInventory() == p.getInventory()){
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        }

        if (event.getView().getTitle().equalsIgnoreCase("Spectator")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            event.setCancelled(true);
            if (event.getSlotType() == InventoryType.SlotType.CONTAINER) {
                Utils.skullTeleport(p, event.getCurrentItem());
            }
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (gameManager.getGameState() == GameState.LOBBY || gameManager.getGameState() == GameState.STARTING) {
            if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR) return;

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {

                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(config.getString("lobby-items.kits.display-name")))) {
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(config.getString("lobby-items.perks.display-name")))) {
                }

                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(config.getString("lobby-items.statistics.display-name")))) {
                    // menuAction.execute(plugin, p, "statistics-inv");
                }
            }
        }
    }
}