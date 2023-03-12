package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.task.*;

public class TaskManager {

    private UHCRun plugin;

    private long delay = 20L;
    private long period = 20L;

    private StartingCd startingCd;
    private MiningCd miningCd;
    private FightingCd fightingCd;
    private DeathMCd deathmatchCd;
    private EndingCd endingCd;

    public TaskManager(UHCRun plugin) {
        this.plugin = plugin;
    }

    public void startStartingCD() {
        this.startingCd = new StartingCd(plugin);
        this.startingCd.runTaskTimer(plugin, delay, period);
    }
    public void stopStartingCD() {
        if (!this.startingCd.isCancelled()) this.startingCd.cancel();
    }

    public void startMiningCD() {
        this.miningCd = new MiningCd(plugin);
        this.miningCd.runTaskTimer(plugin, delay, period);
    }
    public void stopMiningCD() {
        if (!this.miningCd.isCancelled()) this.miningCd.cancel();
    }

    public void startFightingCD() {
        this.fightingCd = new FightingCd(plugin);
        this.fightingCd.runTaskTimer(plugin, delay, period);
    }
    public void stopFightingCD() {
        if (!this.fightingCd.isCancelled()) this.fightingCd.cancel();
    }

    public void startDeathmatchCD() {
        this.deathmatchCd = new DeathMCd(plugin);
        this.deathmatchCd.runTaskTimer(plugin, delay, period);
    }
    public void stopDeathmatchCD() {
        if (!this.deathmatchCd.isCancelled()) this.deathmatchCd.cancel();
    }

    public void startEndingCD() {
        this.endingCd = new EndingCd(plugin);
        this.endingCd.runTaskTimer(plugin, delay, period);
    }
    public void stopEndingCD() {
        if (!this.endingCd.isCancelled()) this.endingCd.cancel();
    }


}
