package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.Permissions;
import me.florixak.uhcrun.game.player.PlayerManager;
import me.florixak.uhcrun.game.player.UHCPlayer;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    private final GameManager gameManager;

    public ChatListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void handlePlayerChat(AsyncPlayerChatEvent event) {
        PlayerManager pm = gameManager.getPlayerManager();

        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = pm.getUHCPlayer(event.getPlayer().getUniqueId());

        String format = gameManager.isPlaying() ? (!uhcPlayer.isDead() ? GameValues.CHAT.IN_GAME_FORMAT
                : GameValues.CHAT.DEAD_FORMAT)
                : GameValues.CHAT.LOBBY_FORMAT;

        if (format == null || format.isEmpty()) return;

        event.setCancelled(true);

        String playerName = uhcPlayer.getName();
        String luckPermsPrefix = uhcPlayer.getLuckPermsPrefix();
        String playerLevel = String.valueOf(uhcPlayer.getData().getUHCLevel());
        String message = p.hasPermission(Permissions.COLOR_CHAT.getPerm()) ? TextUtils.color(event.getMessage()) : event.getMessage();
        String team = uhcPlayer.hasTeam() ? TextUtils.color(uhcPlayer.getTeam().getDisplayName()) : "";

        format = format
                .replace("%player%", playerName)
                .replace("%message%", message)
                .replace("%luckperms-prefix%", TextUtils.color(luckPermsPrefix))
                .replace("%uhc-level%", TextUtils.color(playerLevel))
                .replace("%team%", team);

        String finalFormat = format;

        if (!gameManager.isPlaying()) {
            pm.getOnlineList().forEach(uhcPlayers -> uhcPlayer.sendMessage(TextUtils.color(finalFormat)));
            return;
        }
        if (uhcPlayer.isSpectator()) {
            pm.getSpectatorList().stream().filter(UHCPlayer::isOnline).forEach(uhcPlayers -> uhcPlayer.sendMessage(TextUtils.color(finalFormat)));
            return;
        }
        if (GameValues.TEAM.TEAM_MODE && !message.startsWith("!")) {
            uhcPlayer.getTeam().sendMessage(TextUtils.color(format));
            return;
        }
        pm.getOnlineList().forEach(uhcPlayers -> uhcPlayers.sendMessage(TextUtils.color("&6[GLOBAL] ") + finalFormat.replaceFirst("!", "")));
    }

    @EventHandler
    public void handlePlayerCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String msg = event.getMessage();
        String args[] = msg.split(" ");

        if (GameValues.CHAT.BLOCKED_COMMANDS.contains(event.getMessage().toLowerCase())) {
            event.setCancelled(true);
            p.sendMessage(Messages.NO_PERM.toString());
        }

        if (Bukkit.getServer().getHelpMap().getHelpTopic(args[0]) == null) {
            event.setCancelled(true);
            p.sendMessage(Messages.INVALID_CMD.toString());
        }
    }

}