package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class AutoBroadcastMessages extends BukkitRunnable {

    private UHCRun plugin;
    private FileConfiguration config, messages_config;
    private BroadcastMessageAction broadcastMessageAction;
    private List<String> messages;
    private boolean random;
    private int lastMessage;

    public AutoBroadcastMessages(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages_config = plugin.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();
        this.messages = messages_config.getStringList("Messages.auto-messages");
        this.random = config.getBoolean("auto-broadcast.random-messages");
        this.broadcastMessageAction = new BroadcastMessageAction();

    }

    @Override
    public void run() {

        String message = "";

        if (!random) {
            try {
                message = messages.get(lastMessage + 1);
                lastMessage++;
            } catch (ArrayIndexOutOfBoundsException e) {
                message = messages.get(0);
                lastMessage = 0;
            }
        } else {
            Random ran = new Random();
            int nextMessage = ran.nextInt(messages.size());

            while (nextMessage == lastMessage) {
                nextMessage = ran.nextInt(messages.size());
            }
            message = messages.get(nextMessage);
            lastMessage = nextMessage;
        }
        broadcastMessageAction.execute(plugin, null, TextUtils.color(message
                .replace("%prefix%", messages_config.getString("Messages.prefix")))
        );

    }
}
