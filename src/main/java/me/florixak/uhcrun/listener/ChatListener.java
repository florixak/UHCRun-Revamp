package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.LevelManager;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.utils.TextUtils;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.UUID;

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

        Player player = event.getPlayer();
        String prefix = "";
        String color = "&8";
        String level = "";

        event.setCancelled(true);

        if (config.getBoolean("use-LuckPerms", true)) {
            prefix = LuckPermsProvider.get().getUserManager().getUser(player.getName())
                    .getCachedData().getMetaData().getPrefix();
        }

        if (lvl.getPlayerLevel(player.getUniqueId()) < 10) color = chat.getString("chat.level-color.toNine");
        else if (lvl.getPlayerLevel(player.getUniqueId()) < 20) color = chat.getString("chat.level-color.toNineteen");
        else if (lvl.getPlayerLevel(player.getUniqueId()) < 30) color = chat.getString("chat.level-color.toTwentyNine");
        else if (lvl.getPlayerLevel(player.getUniqueId()) < 40) color = chat.getString("chat.level-color.toThirtyNine");
        else if (lvl.getPlayerLevel(player.getUniqueId()) < 50) color = chat.getString("chat.level-color.toFortyNine");
        else if (lvl.getPlayerLevel(player.getUniqueId()) < 60) color = chat.getString("chat.level-color.toFiftyNine");
        else if (lvl.getPlayerLevel(player.getUniqueId()) < 70) color = chat.getString("chat.level-color.toSixtyNine");
        else if (lvl.getPlayerLevel(player.getUniqueId()) < 80) color = chat.getString("chat.level-color.toSeventyNine");
        else color = chat.getString("chat.level-color.toInfinity");

        level = chat.getBoolean("chat.level-with-brackets", true) ?
                color + "[" + lvl.getPlayerLevel(player.getUniqueId()) + "]" : color + lvl.getPlayerLevel(player.getUniqueId());

        if (!plugin.getGame().isPlaying()) {
            for (UUID uuid : PlayerManager.online) {
                Bukkit.getPlayer(uuid).sendMessage(TextUtils.color(format
                        .replace("%player%", player.getName())
                        .replace("%message%", event.getMessage())
                        .replace("%luckperms-prefix%", prefix)
                        .replace("%level%", level)
                        .replace("%team%", !plugin.getTeams().teams.isEmpty() ? plugin.getTeams().getTeam(player) : "")));

                if (plugin.getGame().isEnding()) {
                    if (event.getMessage().toLowerCase().contains("gg")
                            || event.getMessage().toLowerCase().contains("good game")) {
                        int gg_coins = config.getInt("gg-reward.coins");
                        int gg_xp = config.getInt("gg-reward.level-xp");
                        if (gg_coins != 0) plugin.getStatistics().addMoney(player, gg_coins);
                        if (gg_xp != 0) lvl.addPlayerLevel(uuid, gg_xp);
                        player.sendMessage(Messages.GG_REWARD.toString()
                                .replace("%coins-for-gg%", String.valueOf(gg_coins))
                                .replace("%level-xp-for-gg%", String.valueOf(gg_xp)));
                    }
                }
            }
        } else {

            if (PlayerManager.isAlive(player)) {
                for (UUID uuid : PlayerManager.online) {
                    Bukkit.getPlayer(uuid).sendMessage(TextUtils.color(format
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage())
                            .replace("%luckperms-prefix%", prefix)
                            .replace("%level%", level)
                            .replace("%team%", !plugin.getTeams().teams.isEmpty() ? plugin.getTeams().getTeam(player) : "")));
                }
            }
            else {
                for (UUID uuid : PlayerManager.spectators) {
                    Bukkit.getPlayer(uuid).sendMessage(TextUtils.color(format
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage())
                            .replace("%luckperms-prefix%", prefix)
                            .replace("%level%", level)
                            .replace("%team%", !plugin.getTeams().teams.isEmpty() ? plugin.getTeams().getTeam(player) : "")));
                }
            }
        }
    }
}