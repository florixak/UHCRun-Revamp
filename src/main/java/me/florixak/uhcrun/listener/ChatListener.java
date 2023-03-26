package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.LevelManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import net.luckperms.api.LuckPermsProvider;
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

    private UHCRun plugin;
    private FileConfiguration config, chat;
    private LevelManager lvl;

    private String format;

    public ChatListener(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.chat = plugin.getConfigManager().getFile(ConfigType.CHAT).getConfig();
        this.lvl = plugin.getLevelManager();

        this.format = chat.getString("chat.format");
    }

    @EventHandler
    public void onPlayerCommandProcess(PlayerCommandPreprocessEvent event){

        this.blockedCommands = chat.getStringList("chat.blocked-commands");

        Player p = event.getPlayer();
        String msg = event.getMessage();
        String args[] = msg.split(" ");

        if (this.blockedCommands.contains(event.getMessage().toLowerCase())) {
            event.setCancelled(true);
            p.sendMessage(Messages.NO_PERM.toString());
        }

        if (Bukkit.getServer().getHelpMap().getHelpTopic(args[0]) == null){
            event.setCancelled(true);
            p.sendMessage(Messages.NO_PERM.toString());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){

        UHCPlayer player = plugin.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());
        String prefix = "";
        String color = "&8";
        String level = "";

        event.setCancelled(true);

        if (config.getBoolean("use-LuckPerms", true)) {
            prefix = LuckPermsProvider.get().getUserManager().getUser(player.getName())
                    .getCachedData().getMetaData().getPrefix();
        }

        if (lvl.getPlayerLevel(player) < 10) color = chat.getString("chat.level-color.toNine");
        else if (lvl.getPlayerLevel(player) < 20) color = chat.getString("chat.level-color.toNineteen");
        else if (lvl.getPlayerLevel(player) < 30) color = chat.getString("chat.level-color.toTwentyNine");
        else if (lvl.getPlayerLevel(player) < 40) color = chat.getString("chat.level-color.toThirtyNine");
        else if (lvl.getPlayerLevel(player) < 50) color = chat.getString("chat.level-color.toFortyNine");
        else if (lvl.getPlayerLevel(player) < 60) color = chat.getString("chat.level-color.toFiftyNine");
        else if (lvl.getPlayerLevel(player) < 70) color = chat.getString("chat.level-color.toSixtyNine");
        else if (lvl.getPlayerLevel(player) < 80) color = chat.getString("chat.level-color.toSeventyNine");
        else color = chat.getString("chat.level-color.toInfinity");

        level = chat.getBoolean("chat.level-with-brackets", true) ?
                color + "[" + lvl.getPlayerLevel(player) + "]" : color + lvl.getPlayerLevel(player);

        if (!plugin.getGame().isPlaying()) {
            for (UHCPlayer players : plugin.getPlayerManager().getPlayers()) {
                players.sendMessage(TextUtils.color(format
                        .replace("%player%", player.getName())
                        .replace("%message%", event.getMessage())
                        .replace("%luckperms-prefix%", prefix)
                        .replace("%level%", level)
                        .replace("%team%", !plugin.getTeams().teams.isEmpty() ? player.getTeam() : "")));

                // TODO - gg odměna
            }
        } else {

            for (UHCPlayer players : plugin.getPlayerManager().getPlayers()) {
                if (players.isDead()) {
                    players.sendMessage("&7[DEAD] " + format
                            .replace("%player%", "&7" + player.getName())
                            .replace("%message%", "&8" + event.getMessage())
                            .replace("%luckperms-prefix%", prefix)
                            .replace("%level%", level)
                            .replace("%team%", !plugin.getTeams().teams.isEmpty() ? player.getTeam() : ""));
                    return;
                }
                players.sendMessage(format
                        .replace("%player%", player.getName())
                        .replace("%message%", event.getMessage())
                        .replace("%luckperms-prefix%", prefix)
                        .replace("%level%", level)
                        .replace("%team%", !plugin.getTeams().teams.isEmpty() ? player.getTeam() : ""));
            }
        }
    }
}