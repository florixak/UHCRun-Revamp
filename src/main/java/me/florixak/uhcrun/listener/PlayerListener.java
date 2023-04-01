package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.events.GameKillEvent;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.Utils;
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
            p.kickPlayer("UHC Run is restarting!");
            return;
        }

        UHCPlayer uhcPlayer = new UHCPlayer(p.getUniqueId(), p.getName());

        if (config.getBoolean("MySQL.enabled", true)) plugin.data.createPlayer(uhcPlayer.getPlayer());
        plugin.getStatistics().setData(uhcPlayer);

        plugin.getPlayerManager().addPlayer(uhcPlayer);

        if (plugin.getGame().isPlaying() || uhcPlayer.isDead()) {
            plugin.getGame().setSpectator(uhcPlayer);
            return;
        }

        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setTotalExperience(0);
        p.giveExp(-p.getTotalExperience());
        p.setFlying(false);
        p.setAllowFlight(false);

        plugin.getPlayerManager().clearPlayerInventory(p);
        plugin.getKitsManager().getWaitingKit(uhcPlayer);

        Utils.broadcast(Messages.JOIN.toString().replace("%player%", p.getDisplayName()));
        p.sendMessage(Messages.PLAYERS_TO_START.toString().replace("%min-players%", "" + config.getInt("min-players-to-start")));

        plugin.getGame().checkGame();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = plugin.getPlayerManager().getUHCPlayer(p.getUniqueId());
        event.setQuitMessage(null);

        plugin.getScoreboardManager().removeScoreboard(uhcPlayer.getPlayer());
        plugin.getPlayerManager().removePlayer(uhcPlayer);

        if (!plugin.getGame().isPlaying() || plugin.getGame().isStarting())
            Utils.broadcast(Messages.QUIT.toString().replace("%player%", uhcPlayer.getName()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        // event.getDrops().clear(); TODO drops from kill

        UHCPlayer uhcKiller = !(event.getEntity().getKiller() instanceof Player) ?
                plugin.getPlayerManager().getUHCPlayer(event.getEntity().getKiller().getUniqueId()) :
                null;
        UHCPlayer uhcVictim = plugin.getPlayerManager().getUHCPlayer(event.getEntity().getPlayer().getUniqueId());

        plugin.getServer().getPluginManager().callEvent(new GameKillEvent(uhcKiller, uhcVictim));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        UHCPlayer uhcPlayer = plugin.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

        if (!plugin.getGame().isPlaying() || uhcPlayer.isDead()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        UHCPlayer uhcPlayer = plugin.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

        if (!plugin.getGame().isPlaying() || uhcPlayer.isDead()) {
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