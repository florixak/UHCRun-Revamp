package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.events.GameKillEvent;
import me.florixak.uhcrun.kits.Kits;
import me.florixak.uhcrun.kits.KitsManager;
import me.florixak.uhcrun.perks.Perks;
import me.florixak.uhcrun.perks.PerksManager;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private UHCRun plugin;
    private FileConfiguration config;

    public PlayerListener(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage(null);

        if (config.getBoolean("MySQL.enabled", true)) plugin.data.createPlayer(p);
        plugin.getStatistics().setData(p);

        PlayerManager.online.add(p.getUniqueId());
        plugin.getKitsManager().kits.put(p.getUniqueId(), Kits.NONE);
        PerksManager.perks.put(p.getUniqueId(), Perks.NONE);

        if (plugin.getGame().isEnding()) {
            p.kickPlayer("UHCRun is restarting!");
            return;
        }

        if (plugin.getGame().isPlaying()) {
            p.setHealth(p.getHealthScale());
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.giveExp((int) -(1*Math.pow(10, 1000000000)));
            p.setGameMode(GameMode.SPECTATOR);
            return;
        }

        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.getInventory().clear();
        p.setLevel(0);
        p.setTotalExperience(0);
        p.giveExp(-p.getTotalExperience());
        p.setFlying(false);
        p.setAllowFlight(false);

        plugin.getKitsManager().getWaitingKit(p);

        Bukkit.broadcastMessage(Messages.JOIN.toString().replace("%player%", p.getDisplayName()));
        p.sendMessage(Messages.PLAYERS_TO_START.toString().replace("%min-players%", "" + config.getInt("min-players-to-start")));

        plugin.getGame().checkGame();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        event.setQuitMessage(null);

        plugin.getGame().removeScoreboard();

        if (PlayerManager.isOnline(p)){
            PlayerManager.online.remove(p.getUniqueId());
        }
        if (PlayerManager.kills.containsKey(p.getUniqueId())) {
            PlayerManager.kills.remove(p.getUniqueId());
        }
        if (PlayerManager.isCreator(p)) {
            PlayerManager.creator.remove(p.getUniqueId());
        }
        if (PlayerManager.isDead(p)){
            PlayerManager.dead.remove(p.getUniqueId());
        }

        plugin.getKitsManager().kits.remove(p);
        PerksManager.perks.remove(p);

        if (!plugin.getGame().isPlaying() || plugin.getGame().isStarting())
            Utils.broadcast(Messages.QUIT.toString().replace("%player%", p.getDisplayName()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        plugin.getServer().getPluginManager().callEvent(new GameKillEvent(event.getEntity().getKiller(), event.getEntity().getPlayer()));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING
                || !PlayerManager.isAlive(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void disablePortals(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
        }
    }
}