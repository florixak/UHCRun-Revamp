package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.tasks.*;
import org.bukkit.Bukkit;

public class TaskManager {

    private final GameManager gameManager;

    private final long delay = 0L;
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
        this.startingCd.runTaskTimer(UHCRevamp.getInstance(), delay, period);
    }

    public void stopStartingCD() {
        if (startingCd == null) this.startingCd.cancel();
    }

    public void startMiningCD() {
        this.miningCd = new MiningCD(gameManager);
        this.miningCd.runTaskTimer(UHCRevamp.getInstance(), delay, period);
    }

    public void startPvPCD() {
        this.pvpCD = new PvPCD(gameManager);
        this.pvpCD.runTaskTimer(UHCRevamp.getInstance(), delay, period);
    }

    public void startDeathmatchCD() {
        this.deathmatchCd = new DeathmatchCD(gameManager);
        this.deathmatchCd.runTaskTimer(UHCRevamp.getInstance(), delay, period);
    }

    public void startDeathmatchResist() {
        this.deathmatchResist = new DeathmatchResist(gameManager);
        this.deathmatchResist.runTaskTimer(UHCRevamp.getInstance(), delay, period);
    }

    public void startEndingCD() {
        this.endingCd = new EndingCD();
        this.endingCd.runTaskTimer(UHCRevamp.getInstance(), delay, period);
    }

    public void runActivityRewards() {
        if (GameValues.ACTIVITY_REWARDS.ENABLED) {
            int interval = GameValues.ACTIVITY_REWARDS.INTERVAL * 20;
            new ActivityRewards(gameManager).runTaskTimer(UHCRevamp.getInstance(), delay, interval);
        }
    }

    public void runAutoBroadcast() {
        if (GameValues.BROADCAST.ENABLED) {
            int interval = GameValues.BROADCAST.INTERVAL * 20;
            new AutoBCMessage(gameManager).runTaskTimer(UHCRevamp.getInstance(), delay, interval);
        }
    }

    public void runGameChecking() {
        this.gameChecking = new GameChecking(gameManager);
        this.gameChecking.runTaskTimer(UHCRevamp.getInstance(), delay, period);
    }

    public void runScoreboardUpdate() {
        this.scoreboardUpdate = new ScoreboardUpdate(gameManager);
        this.scoreboardUpdate.runTaskTimer(UHCRevamp.getInstance(), 0L, period);
    }

    public void onDisable() {
        Bukkit.getScheduler().getActiveWorkers().clear();
        Bukkit.getScheduler().getPendingTasks().clear();
    }

}
