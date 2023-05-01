package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class GameChecking extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;

    private int minPlayers;

    public GameChecking(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        this.minPlayers = config.getInt("settings.game.min-players-to-start");
    }

    @Override
    public void run() {

        switch (gameManager.getGameState()) {
            case LOBBY:
                if (Bukkit.getOnlinePlayers().size() >= minPlayers) {
                    gameManager.setGameState(GameState.STARTING);
                }
                break;
            case STARTING:
                if (gameManager.isForceStarted()) {
                    return;
                }
                if (Bukkit.getOnlinePlayers().size() < minPlayers) {
                    gameManager.getTaskManager().stopStartingCD();
                    gameManager.setGameState(GameState.LOBBY);
                }
                break;
            case MINING:
            case FIGHTING:
            case DEATHMATCH:
                if (gameManager.isForceStarted()) {
                    return;
                }
                if (Bukkit.getOnlinePlayers().size() < minPlayers) {
                    gameManager.setGameState(GameState.ENDING);
                }
                break;
            case ENDING:
                break;
        }
    }
}