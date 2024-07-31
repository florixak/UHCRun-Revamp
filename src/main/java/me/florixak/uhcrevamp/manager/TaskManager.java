package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.tasks.*;
import org.bukkit.Bukkit;

public class TaskManager {

	private final GameManager gameManager;

	private final int delay = 0;
	private final int period = 20;

	private StartingPhaseTask startingPhaseTask;
	private MiningPhaseTask miningPhaseTask;
	private PvPPhaseTask pvpCD;
	private DeathmatchPhaseTask deathmatchPhaseTask;
	private ResistanceTask resistanceTask;
	private EndingPhaseTask endingPhaseTask;

	private GameCheckTack gameCheckTack;
	private BoardsUpdateTask boardsUpdateTask;
	private PlayerTimeTask playerTimeTask;

	public TaskManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void startStartingTask() {
		this.startingPhaseTask = new StartingPhaseTask(gameManager);
		this.startingPhaseTask.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public void cancelStartingTask() {
		if (startingPhaseTask == null) this.startingPhaseTask.cancel();
	}

	public void startMiningTask() {
		if (startingPhaseTask != null) startingPhaseTask.cancel();
		this.miningPhaseTask = new MiningPhaseTask(gameManager);
		this.miningPhaseTask.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public void startPvPTask() {
		if (miningPhaseTask != null) miningPhaseTask.cancel();
		this.pvpCD = new PvPPhaseTask(gameManager);
		this.pvpCD.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public void startDeathmatchTask() {
		if (pvpCD != null) pvpCD.cancel();
		this.deathmatchPhaseTask = new DeathmatchPhaseTask(gameManager);
		this.deathmatchPhaseTask.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public void startResistanceTask() {
		gameManager.setResistance(true);
		this.resistanceTask = new ResistanceTask(gameManager);
		this.resistanceTask.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public ResistanceTask getResistanceTask() {
		return resistanceTask;
	}

	public void startEndingTask() {
		if (deathmatchPhaseTask != null) deathmatchPhaseTask.cancel();
		if (resistanceTask != null) resistanceTask.cancel();
		this.endingPhaseTask = new EndingPhaseTask();
		this.endingPhaseTask.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public void runGameCheckTask() {
		this.gameCheckTack = new GameCheckTack(gameManager);
		this.gameCheckTack.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public void runScoreboardUpdateTask() {
		this.boardsUpdateTask = new BoardsUpdateTask(gameManager);
		this.boardsUpdateTask.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public void runPlayerTimeTask() {
		if (!GameValues.ACTIVITY_REWARDS.ENABLED) return;
		this.playerTimeTask = new PlayerTimeTask(gameManager);
		this.playerTimeTask.runTaskTimer(UHCRevamp.getInstance(), delay, period);
	}

	public PlayerTimeTask getPlayerTimeTask() {
		return playerTimeTask;
	}


	public void onDisable() {
		Bukkit.getScheduler().getActiveWorkers().clear();
		Bukkit.getScheduler().getPendingTasks().clear();
		if (startingPhaseTask != null) startingPhaseTask.cancel();
		if (miningPhaseTask != null) miningPhaseTask.cancel();
		if (pvpCD != null) pvpCD.cancel();
		if (deathmatchPhaseTask != null) deathmatchPhaseTask.cancel();
		if (resistanceTask != null) resistanceTask.cancel();
		if (endingPhaseTask != null) endingPhaseTask.cancel();
		if (gameCheckTack != null) gameCheckTack.cancel();
		if (boardsUpdateTask != null) boardsUpdateTask.cancel();
		if (playerTimeTask != null) playerTimeTask.cancel();
	}

}
