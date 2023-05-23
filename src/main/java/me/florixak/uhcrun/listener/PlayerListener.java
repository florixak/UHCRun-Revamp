package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.listener.events.GameKillEvent;
import me.florixak.uhcrun.game.GameManager;
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

    private GameManager gameManager;
    private FileConfiguration config;

    public PlayerListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player p = event.getPlayer();
        event.setJoinMessage(null);

        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getOrCreateHOCPlayer(p.getUniqueId());
        gameManager.getPlayerManager().addPlayer(uhcPlayer);

        if (gameManager.getGameManager().isPlaying() || uhcPlayer.isDead()) {
            gameManager.getPlayerManager().setSpectator(uhcPlayer);
            return;
        }

        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setTotalExperience(0);
        p.giveExp(-p.getTotalExperience());

        gameManager.getPlayerManager().teleport(p);

        gameManager.getPlayerManager().clearPlayerInventory(p);
        gameManager.getKitsManager().getWaitingKit(uhcPlayer);

        Utils.broadcast(Messages.JOIN.toString()
                .replace("%player%", p.getDisplayName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())));
        p.sendMessage(Messages.PLAYERS_TO_START.toString()
                .replace("%min-players%", "" + config.getInt("min-players-to-start")));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        event.setQuitMessage(null);

        gameManager.getScoreboardManager().removeScoreboard(uhcPlayer.getPlayer());

        if (!gameManager.isPlaying()) {
            Utils.broadcast(Messages.QUIT.toString()
                    .replace("%player%", uhcPlayer.getName())
                    .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()-1)));
            gameManager.getPlayerManager().removePlayer(uhcPlayer);
        } else {
            if (!gameManager.areStatisticsAddedOnEnd()) {
                uhcPlayer.getData().addLose(1);
                uhcPlayer.getData().addDeaths(1);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        UHCPlayer uhcKiller = null;
        if (event.getEntity().getKiller() instanceof Player) {
            uhcKiller = gameManager.getPlayerManager().getUHCPlayer(event.getEntity().getKiller().getUniqueId());
        }
        UHCPlayer uhcVictim = gameManager.getPlayerManager().getUHCPlayer(event.getEntity().getPlayer().getUniqueId());

        if (gameManager.isDeathChestEnabled()) {
            gameManager.getDeathChestManager().createDeathChest(event.getEntity().getPlayer(), event.getDrops());
        }

        Bukkit.getServer().getPluginManager().callEvent(new GameKillEvent(uhcKiller, uhcVictim));
        event.getDrops().clear();
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

        if (!gameManager.isPlaying() || uhcPlayer.isDead()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

        if (!gameManager.isPlaying() || uhcPlayer.isDead()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void disablePortals(PlayerTeleportEvent event) {
        if (!gameManager.isNetherAllowed() && event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
        }
    }
}