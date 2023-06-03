package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class ChatListener implements Listener {

    private List<String> blockedCommands;

    private final GameManager gameManager;
    private final FileConfiguration config;

    public ChatListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        event.setCancelled(true);

        PlayerManager pm = gameManager.getPlayerManager();

        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = pm.getUHCPlayer(event.getPlayer().getUniqueId());

        String format = gameManager.isPlaying() ? (!uhcPlayer.isDead() ? config.getString("settings.chat.in-game-format")
                : config.getString("settings.chat.dead-format"))
                : config.getString("settings.chat.lobby-format");

        if (format == null || format.isEmpty()) return;

        String playerName = uhcPlayer.getName();
        String lp_prefix = uhcPlayer.getLuckPermsPrefix();
        String playerLevel = String.valueOf(uhcPlayer.getData().getUHCLevel());
        String message = p.hasPermission("uhcrun.color-chat") ? TextUtils.color(event.getMessage()) : event.getMessage();
        String team = uhcPlayer.getTeam() != null ? TextUtils.color(uhcPlayer.getTeam().getDisplayName()) : "";

        String formattedMessage = format
                .replace("%player%", playerName)
                .replace("%message%", message)
                .replace("%luckperms-prefix%", TextUtils.color(lp_prefix))
                .replace("%uhc-level%", TextUtils.color(playerLevel))
                .replace("%team%", team);

        for (UHCPlayer players : pm.getPlayers()) {

            if (!gameManager.isPlaying()) {
                players.sendMessage(formattedMessage);
                return;
            }

            if (players.isDead()) {
                players.sendMessage(formattedMessage);
                return;
            }
            players.sendMessage(formattedMessage);
        }
    }

    @EventHandler
    public void onPlayerCommandProcess(PlayerCommandPreprocessEvent event){

        this.blockedCommands = config.getStringList("settings.chat.blocked-commands");

        Player p = event.getPlayer();
        String msg = event.getMessage();
        String args[] = msg.split(" ");

        if (this.blockedCommands.contains(event.getMessage().toLowerCase())) {
            event.setCancelled(true);
            p.sendMessage(Messages.NO_PERM.toString());
        }

        if (Bukkit.getServer().getHelpMap().getHelpTopic(args[0]) == null){
            event.setCancelled(true);
            p.sendMessage(Messages.INVALID_CMD.toString());
        }
    }

}