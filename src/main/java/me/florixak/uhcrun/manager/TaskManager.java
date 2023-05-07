package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class TaskManager {

    private GameManager gameManager;
    private FileConfiguration config;

    private final long delay = 20L;
    private final long period = 20L;

    private StartingCD startingCd;
    private MiningCD miningCd;
    private FightingCD fightingCd;
    private DeathmatchCD deathmatchCd;
    private EndingCD endingCd;

    private GameChecking gameChecking;
    private ScoreboardUpdate scoreboardUpdate;

    public TaskManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public void startStartingCD() {
        this.startingCd = new StartingCD(gameManager);
        this.startingCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }
    public void stopStartingCD() {
        if (!this.startingCd.isCancelled()) this.startingCd.cancel();
    }

    public void startMiningCD() {
        this.miningCd = new MiningCD(gameManager);
        this.miningCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }
    public void stopMiningCD() {
        if (!this.miningCd.isCancelled()) this.miningCd.cancel();
    }

    public void startFightingCD() {
        this.fightingCd = new FightingCD(gameManager);
        this.fightingCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }
    public void stopFightingCD() {
        if (!this.fightingCd.isCancelled()) this.fightingCd.cancel();
    }

    public void startDeathmatchCD() {
        this.deathmatchCd = new DeathmatchCD(gameManager);
        this.deathmatchCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }
    public void stopDeathmatchCD() {
        if (!this.deathmatchCd.isCancelled()) this.deathmatchCd.cancel();
    }

    public void startEndingCD() {
        this.endingCd = new EndingCD(gameManager);
        this.endingCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }
    public void stopEndingCD() {
        if (!this.endingCd.isCancelled()) this.endingCd.cancel();
    }

    public void runActivityRewards() {
        if (config.getBoolean("settings.rewards-per-time.enabled", true)) {
            int interval = config.getInt("settings.rewards-per-time.period")*20;
            new ActivityRewards(gameManager).runTaskTimer(UHCRun.getInstance(), delay, interval);
        }
    }
    public void runAutoBroadcast() {
        if (config.getBoolean("settings.auto-broadcast.enabled", true)) {
            int interval = config.getInt("settings.auto-broadcast.period")*20;
            new AutoBCMessage(gameManager).runTaskTimer(UHCRun.getInstance(), delay, interval);
        }
    }

    public void runGameChecking() {
        this.gameChecking = new GameChecking(gameManager);
        this.gameChecking.runTaskTimer(UHCRun.getInstance(), this.delay, this.period);
    }

    public void runScoreboardUpdate() {
        this.scoreboardUpdate = new ScoreboardUpdate(gameManager);
        this.scoreboardUpdate.runTaskTimer(UHCRun.getInstance(), this.delay, this.period);
    }

    public void stopScoreboardUpdate() {
        if (!this.scoreboardUpdate.isCancelled()) this.scoreboardUpdate.cancel();
    }

    public void onDisable() {
        Bukkit.getScheduler().getActiveWorkers().clear();
        Bukkit.getScheduler().getPendingTasks().clear();
    }

}
