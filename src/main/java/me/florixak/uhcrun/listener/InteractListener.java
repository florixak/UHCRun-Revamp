package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.BungeeAction;
import me.florixak.uhcrun.action.actions.MenuAction;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utility.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InteractListener implements Listener {

    private UHCRun plugin;
    private MenuAction menuAction;

    public InteractListener(UHCRun plugin) {
        this.plugin = plugin;
        this.menuAction = new MenuAction();
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleporterClick(InventoryClickEvent event) {

        Player p = (Player) event.getWhoClicked();

        if (!PlayerManager.isDead(p)) return;

        if (event.getClickedInventory() == p.getInventory()){
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        }

        if (event.getView().getTitle().equalsIgnoreCase("Spectator")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            event.setCancelled(true);
            if (event.getSlotType() == InventoryType.SlotType.CONTAINER) {
                plugin.getUtilities().skullTeleport(p, event.getCurrentItem());
            }
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (plugin.getGame().gameState == GameState.WAITING || plugin.getGame().gameState == GameState.STARTING) {
            if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR) return;

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {

                // LOBBY
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(TextUtil.color(plugin.getConfigManager()
                        .getFile(ConfigType.SETTINGS).getConfig().getString("lobby-items.kits.display-name")))) {
                    menuAction.execute(plugin, p, "kits-inv");
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(TextUtil.color(plugin.getConfigManager()
                        .getFile(ConfigType.SETTINGS).getConfig().getString("lobby-items.perks.display-name")))) {
                    menuAction.execute(plugin, p, "perks-inv");
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(TextUtil.color(plugin.getConfigManager()
                        .getFile(ConfigType.SETTINGS).getConfig().getString("lobby-items.start.display-name")))) {
                    if (plugin.getGame().gameState != GameState.WAITING) {
                        p.sendMessage(Messages.GAME_ALREADY_STARTING.toString());
                        return;
                    }
                    plugin.getGame().setGameState(GameState.STARTING);
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(TextUtil.color(plugin.getConfigManager()
                        .getFile(ConfigType.SETTINGS).getConfig().getString("lobby-items.vote.display-name")))) {
                    menuAction.execute(plugin, p, "vote-inv");
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(TextUtil.color(plugin.getConfigManager()
                        .getFile(ConfigType.SETTINGS).getConfig().getString("lobby-items.statistics.display-name")))) {
                    menuAction.execute(plugin, p, "statistics-inv");
                }

                // CREATOR
                if (PlayerManager.isCreator(p)) {
                    if (item.getType() == Material.STICK) {
                        plugin.getLobbyManager().setWaitingLobby(p.getLocation());
                        p.sendMessage(Messages.SETLOBBY_WAITING.toString());
                    }
                    if (item.getType() == Material.BLAZE_ROD) {
                        plugin.getLobbyManager().setEndingLobby(p.getLocation());
                        p.sendMessage(Messages.SETLOBBY_ENDING.toString());
                    }
                }
            }
        }

        if (PlayerManager.isDead(p)) {
            if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR) return;

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(TextUtil.color(plugin.getConfigManager()
                        .getFile(ConfigType.SETTINGS).getConfig().getString("spectator-items.spectator.display-name")))) {

                    Inventory inv = Bukkit.createInventory(null, 35, "Teleporter");

                    for (UUID uuid : PlayerManager.online) {
                        String name = Bukkit.getPlayer(uuid).getDisplayName();
                        inv.addItem(plugin.getUtilities().getPlayerHead(name, name));
                    }
                    p.openInventory(inv);
                }

                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(TextUtil.color(plugin.getConfigManager()
                        .getFile(ConfigType.SETTINGS).getConfig().getString("spectator-items.leave.display-name")))) {
                    new BungeeAction().execute(plugin, p, "lobby");
                }
            }
        }
    }
}