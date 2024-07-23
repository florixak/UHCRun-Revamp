package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class AutoBCMessage extends BukkitRunnable {

    private final FileConfiguration messages_config;
    private List<String> messages;

    private final boolean random;
    private int lastMessage;

    public AutoBCMessage(GameManager gameManager) {
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages_config = gameManager.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();
        this.messages = messages_config.getStringList("Messages.auto-messages");
        this.random = config.getBoolean("settings.auto-broadcast.random-messages", false);
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
        Utils.broadcast(TextUtils.color(message.replace("%prefix%", messages_config.getString("Messages.prefix", ""))));
    }
}
