package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.tasks.*;
import org.bukkit.Bukkit;

public class TaskManager {

    private final GameManager gameManager;

    private final long delay = 20L;
    private final long period = 20L;

    private StartingCD startingCd;
    private MiningCD miningCd;
    private PvPCD pvpCD;
    private DeathmatchCD deathmatchCd;
    private DeathmatchResist deathmatchResist;
    private EndingCD endingCd;

    private GameChecking gameChecking;
    private ScoreboardUpdate scoreboardUpdate;

    public TaskManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void startStartingCD() {
        this.startingCd = new StartingCD(gameManager);
        this.startingCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }

    public void stopStartingCD() {
        if (startingCd == null || !startingCd.isCancelled()) this.startingCd.cancel();
    }

    public void startMiningCD() {
        this.miningCd = new MiningCD(gameManager);
        this.miningCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }

    public void startPvPCD() {
        this.pvpCD = new PvPCD(gameManager);
        this.pvpCD.runTaskTimer(UHCRun.getInstance(), delay, period);
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
        this.endingCd = new EndingCD();
        this.endingCd.runTaskTimer(UHCRun.getInstance(), delay, period);
    }

    public void runActivityRewards() {
        if (GameValues.ACTIVITY_REWARDS_ENABLED) {
            int interval = GameValues.ACTIVITY_REWARDS_INTERVAL * 20;
            new ActivityRewards(gameManager).runTaskTimer(UHCRun.getInstance(), delay, interval);
        }
    }

    public void runAutoBroadcast() {
        if (GameValues.BROADCAST_ENABLED) {
            int interval = GameValues.BROADCAST_INTERVAL * 20;
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
