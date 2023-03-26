package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.events.GameKillEvent;
import me.florixak.uhcrun.perks.PerksManager;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
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

        if (plugin.getGame().isEnding()) {
            p.kickPlayer("UHCRun is restarting!");
            return;
        }

        UHCPlayer player = new UHCPlayer(p.getUniqueId(), p.getName());

        if (config.getBoolean("MySQL.enabled", true)) plugin.data.createPlayer(player.getPlayer());
        plugin.getStatistics().setData(player);

        plugin.getPlayerManager().addPlayer(player);

        if (plugin.getGame().isPlaying() || player.isDead()) {
            plugin.getGame().setSpectator(player);
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

        plugin.getKitsManager().getWaitingKit(player);

        Bukkit.broadcastMessage(Messages.JOIN.toString().replace("%player%", p.getDisplayName()));
        p.sendMessage(Messages.PLAYERS_TO_START.toString().replace("%min-players%", "" + config.getInt("min-players-to-start")));

        plugin.getGame().checkGame();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UHCPlayer player = plugin.getPlayerManager().getUHCPlayer(p.getUniqueId());
        event.setQuitMessage(null);

        plugin.getScoreboardManager().removeScoreboard(p);
        plugin.getPlayerManager().removePlayer(player);

        /*if (PlayerManager.isCreator(p)) {
            PlayerManager.creator.remove(p.getUniqueId());
        }
        if (PlayerManager.isSpectator(p)){
            PlayerManager.spectators.remove(p.getUniqueId());
        }*/

        if (!plugin.getGame().isPlaying() || plugin.getGame().isStarting())
            Utils.broadcast(Messages.QUIT.toString().replace("%player%", p.getDisplayName()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        UHCPlayer killer = event.getEntity().getKiller() != null ?
                plugin.getPlayerManager().getUHCPlayer(event.getEntity().getKiller().getUniqueId()) :
                null;
        UHCPlayer victim = plugin.getPlayerManager().getUHCPlayer(event.getEntity().getPlayer().getUniqueId());

        plugin.getServer().getPluginManager().callEvent(new GameKillEvent(killer, victim));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING
                //|| !PlayerManager.isAlive(event.getPlayer())
        ) {
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