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
    private DeathmatchResist deathmatchResist;
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

    public void startFightingCD() {
        this.fightingCd = new FightingCD(gameManager);
        this.fightingCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }

    public void startDeathmatchCD() {
        this.deathmatchCd = new DeathmatchCD(gameManager);
        this.deathmatchCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }

    public void startDeathmatchResist() {
        this.deathmatchResist = new DeathmatchResist(gameManager);
        this.deathmatchResist.runTaskTimer(UHCRun.getInstance(), delay, period);
    }

    public void startEndingCD() {
        this.endingCd = new EndingCD(gameManager);
        this.endingCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }

    public void runActivityRewards() {
        if (config.getBoolean("settings.rewards.playing-time.enabled")) {
            int interval = config.getInt("settings.rewards.playing-time.period")*20;
            new PlayingRewards(gameManager).runTaskTimer(UHCRun.getInstance(), delay, interval);
        }
    }
    public void runAutoBroadcast() {
        if (config.getBoolean("settings.auto-broadcast.enabled")) {
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

    public void onDisable() {
        Bukkit.getScheduler().getActiveWorkers().clear();
        Bukkit.getScheduler().getPendingTasks().clear();
    }

}
