package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class MiningCD extends BukkitRunnable {

    private final GameManager gameManager;

    public static int countdown;

    public MiningCD(GameManager gameManager) {
        this.gameManager = gameManager;
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        countdown = config.getInt("settings.game.countdowns.mining");
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            gameManager.setGameState(GameState.PVP);
            return;
        }

        if (countdown <= 10) {
            Utils.broadcast(Messages.PVP_IN.toString()
                    .replace("%countdown%", TimeUtils.getFormattedTime(countdown)));
        }
        countdown--;
    }

}
