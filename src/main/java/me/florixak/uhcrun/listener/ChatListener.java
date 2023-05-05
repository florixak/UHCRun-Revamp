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

    private GameManager gameManager;
    private FileConfiguration config;

    private String format;

    public ChatListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        this.format = config.getString("settings.chat.format");
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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        event.setCancelled(true);

        PlayerManager pm = gameManager.getPlayerManager();
        UHCPlayer uhcPlayer = pm.getUHCPlayer(event.getPlayer().getUniqueId());

        String prefix = "";
        String color = "&7";
        String level = "";

        if (config.getBoolean("settings.addons.use-LuckPerms", true)) {
            // prefix = Utils.getLuckPermsPrefix(uhcPlayer.getPlayer());
        }

        level = config.getBoolean("settings.chat.level-with-brackets", true) ?
                color + "[" + uhcPlayer.getData().getLevel() + "]" : String.valueOf(uhcPlayer.getData().getLevel());

        if (!gameManager.isPlaying()) {
            for (UHCPlayer players : pm.getPlayers()) {
                players.sendMessage(TextUtils.color(format
                        .replace("%player%", uhcPlayer.getName())
                        .replace("%message%", event.getMessage())
                        .replace("%luckperms-prefix%", prefix)
                        .replace("%level%", level)
                        .replace("%team%", uhcPlayer.getTeam() != null ? uhcPlayer.getTeam().getDisplayName() : "")));
                // TODO - gg odměna
            }
            return;
        }

        for (UHCPlayer players : pm.getPlayers()) {
            if (players.isDead()) {
                players.sendMessage("&7[DEAD] " + format
                        .replace("%player%", "&7" + uhcPlayer.getName())
                        .replace("%message%", "&8" + event.getMessage())
                        .replace("%luckperms-prefix%", prefix)
                        .replace("%level%", level)
                        .replace("%team%", uhcPlayer.getTeam() != null ? uhcPlayer.getTeam().getName() : ""));
                return;
            }
            players.sendMessage(format
                    .replace("%player%", uhcPlayer.getName())
                    .replace("%message%", event.getMessage())
                    .replace("%luckperms-prefix%", prefix)
                    .replace("%level%", level)
                    .replace("%team%", uhcPlayer.getTeam() != null ? uhcPlayer.getTeam().getName() : ""));
        }

    }
}