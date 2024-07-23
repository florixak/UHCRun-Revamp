package me.florixak.uhcrevamp.game.player;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.Permissions;
import me.florixak.uhcrevamp.listener.events.GameKillEvent;
import me.florixak.uhcrevamp.manager.lobby.LobbyType;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private final GameManager gameManager;

    public PlayerListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void handleLogin(PlayerLoginEvent event) {

        boolean isPlaying = gameManager.isPlaying();
        boolean isFull = gameManager.isGameFull();
        boolean isEnding = gameManager.isEnding();
        if (isEnding) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Messages.GAME_ENDED.toString());
        } else if (!isPlaying && isFull) {
            if (!event.getPlayer().hasPermission(Permissions.RESERVED_SLOT.getPerm())) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, Messages.GAME_FULL.toString());
                return;
            }
            UHCPlayer randomUHCPlayer = gameManager.getPlayerManager().getUHCPlayerWithoutPerm(Permissions.RESERVED_SLOT.getPerm());
            if (randomUHCPlayer == null) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, Messages.GAME_FULL.toString());
                return;
            }
            randomUHCPlayer.kick(Messages.KICK_DUE_RESERVED_SLOT.toString());
            event.allow();

        } else if (isPlaying && isFull) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, Messages.GAME_FULL.toString());
        }
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {

        Player p = event.getPlayer();
        event.setJoinMessage(null);

        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getOrCreateUHCPlayer(p.getUniqueId());
        gameManager.getPlayerManager().addPlayer(uhcPlayer);

        boolean isPlaying = gameManager.isPlaying();

        if (isPlaying) {
            uhcPlayer.setSpectator();
            return;
        }

        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.giveExp(-p.getTotalExperience());
        p.setTotalExperience(0);
        p.setLevel(0);

        p.teleport(gameManager.getLobbyManager().getLocation(LobbyType.WAITING));

        uhcPlayer.clearInventory();
        gameManager.getKitsManager().getLobbyKit(uhcPlayer);

        Utils.broadcast(Messages.JOIN.toString()
                .replace("%player%", p.getDisplayName())
                .replace("%online%", String.valueOf(gameManager.getPlayerManager().getOnlineList().size())));
        p.sendMessage(Messages.PLAYERS_TO_START.toString()
                .replace("%min-players%", "" + GameValues.GAME.MIN_PLAYERS));
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {

        Player p = event.getPlayer();
        event.setQuitMessage(null);

        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        gameManager.getScoreboardManager().removeScoreboard(uhcPlayer.getPlayer());


        if (gameManager.getGameState().equals(GameState.LOBBY) || gameManager.getGameState().equals(GameState.STARTING)) {
            Utils.broadcast(Messages.QUIT.toString()
                    .replace("%player%", uhcPlayer.getName())
                    .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size() - 1)));
            uhcPlayer.leaveTeam();
        } else if (!GameValues.GAME.STATS_ADD_ON_END && gameManager.isPlaying()) {
            uhcPlayer.getData().addDeaths(1);
            uhcPlayer.getData().addLose(1);
            uhcPlayer.getData().setGamesPlayed();
        }

        gameManager.getPlayerManager().removePlayer(uhcPlayer);
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        UHCPlayer uhcKiller = null;
        if (event.getEntity().getKiller() instanceof Player) {
            uhcKiller = gameManager.getPlayerManager().getUHCPlayer(event.getEntity().getKiller().getUniqueId());
        }
        UHCPlayer uhcVictim = gameManager.getPlayerManager().getUHCPlayer(event.getEntity().getPlayer().getUniqueId());

        if (GameValues.DEATH_CHEST.ENABLED) {
            gameManager.getDeathChestManager().createDeathChest(event.getEntity().getPlayer(), event.getDrops());
            event.getDrops().clear();
        }

        Bukkit.getServer().getPluginManager().callEvent(new GameKillEvent(uhcKiller, uhcVictim));
    }

    @EventHandler
    public void handleItemDrop(PlayerDropItemEvent event) {
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

        if (!gameManager.isPlaying() || uhcPlayer.isDead() || gameManager.getGameState().equals(GameState.ENDING)) {
            event.setCancelled(true);
        }
    }

    @Deprecated
    @EventHandler
    public void handleItemPickUp(PlayerPickupItemEvent event) {
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());

        if (!gameManager.isPlaying() || uhcPlayer.isDead() || gameManager.getGameState().equals(GameState.ENDING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePortalTeleport(PlayerTeleportEvent event) {
        if (!GameValues.GAME.NETHER_ENABLED && event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
        }
    }
}