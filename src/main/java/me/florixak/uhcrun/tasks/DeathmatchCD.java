package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathmatchCD extends BukkitRunnable {

    private final GameManager gameManager;
    public static int countdown;

    public DeathmatchCD(GameManager gameManager) {
        this.gameManager = gameManager;
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        countdown = config.getInt("settings.game.countdowns.deathmatch");
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            gameManager.setGameState(GameState.ENDING);
            return;
        }

        countdown--;
    }
}
