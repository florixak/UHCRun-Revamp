package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameConst;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.game.kits.KitsManager;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingCD extends BukkitRunnable {

    private final GameManager gameManager;
    private final KitsManager kitsManager;

    private static int countdown;
    private final int startWarning;

    public StartingCD(GameManager gameManager) {
        this.gameManager = gameManager;
        this.kitsManager = gameManager.getKitsManager();
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        countdown = GameConst.STARTING_COUNTDOWN;
        this.startWarning = config.getInt("settings.game.starting-message-at");
    }

    public static int getCountdown() {
        return countdown;
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            Utils.broadcast(Messages.GAME_STARTED.toString());
            Bukkit.getOnlinePlayers().forEach(player -> gameManager.getSoundManager().playGameStarted(player));
            gameManager.setGameState(GameState.MINING);
            return;
        }

        if (kitsManager.willOpenWhenStarting() && countdown == kitsManager.getOpenWhenStartingAt()) {
            gameManager.getPlayerManager().getOnlineList()
                    .forEach(uhcPlayer -> gameManager.getGuiManager().getInventory("kits").openInv(uhcPlayer.getPlayer()));
        }

        if (countdown <= startWarning) {
            Utils.broadcast(Messages.GAME_STARTING.toString()
                    .replace("%countdown%", "" + TimeUtils.getFormattedTime(countdown)));
            Bukkit.getOnlinePlayers().forEach(player -> gameManager.getSoundManager().playStartingSound(player));
        }
        countdown--;
    }

}