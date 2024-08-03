package me.florixak.uhcrevamp.game.assists;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class DamageTrackerManager {

	private static final int ASSIST_COOLDOWN = 180;
	private final Map<UHCPlayer, Integer> trackerCooldown = new HashMap<>();
	private final Map<UHCPlayer, UHCPlayer> trackerMap = new HashMap<>();

	private final Map<UHCPlayer, Integer> assistCooldown = new HashMap<>();
	private final Map<UHCPlayer, UHCPlayer> assistMap = new HashMap<>();

	public DamageTrackerManager() {
	}

	public void addTrackerOn(final UHCPlayer victim, final UHCPlayer attacker) {
		trackerMap.put(victim, attacker);
		trackerCooldown.put(victim, ASSIST_COOLDOWN);
		attacker.sendMessage("Damaged " + victim.getName() + ", will receive assist if they die within few minutes.");
		victim.sendMessage("You have been damaged by " + attacker.getName() + ".");

		runCooldown(victim, attacker, trackerCooldown, trackerMap);
	}

	public void addAssist(final UHCPlayer victim, final UHCPlayer assistant) {
		assistMap.put(victim, assistant);
		assistCooldown.put(victim, ASSIST_COOLDOWN);

		runCooldown(victim, assistant, assistCooldown, assistMap);
	}

	public void removeFromTracker(final UHCPlayer victim) {
		trackerMap.remove(victim);
		trackerCooldown.remove(victim);
	}

	public void removeFromAssist(final UHCPlayer victim) {
		assistMap.remove(victim);
		assistCooldown.remove(victim);
	}

	public boolean isInTracker(final UHCPlayer victim) {
		return trackerMap.containsKey(victim);
	}

	public boolean isInAssist(final UHCPlayer victim) {
		return assistMap.containsKey(victim);
	}

	public UHCPlayer getAttacker(final UHCPlayer victim) {
		return trackerMap.get(victim);
	}

	public UHCPlayer getAssistant(final UHCPlayer victim) {
		return assistMap.get(victim);
	}

	public void onDead(final UHCPlayer victim) {
		if (isInTracker(victim)) {
			removeFromTracker(victim);
		}
		if (isInAssist(victim)) {
			removeFromAssist(victim);
		}
	}

	public void onDisable() {
		trackerCooldown.clear();
		trackerMap.clear();
		assistCooldown.clear();
		assistMap.clear();
	}

	public void runCooldown(final UHCPlayer victim, final UHCPlayer attacker, final Map<UHCPlayer, Integer> cooldown, final Map<UHCPlayer, UHCPlayer> map) {
		new BukkitRunnable() {
			@Override
			public void run() {
				synchronized (cooldown) {
					if (assistCooldown.containsKey(victim) && trackerMap.containsKey(victim)) {
						assistCooldown.put(victim, assistCooldown.get(victim) - 1);
						if (assistCooldown.get(victim) <= 0 || !attacker.isOnline()) {
							removeFromTracker(victim);
							cancel();
						}
					} else {
						cancel();
					}
				}
			}
		}.runTaskTimer(UHCRevamp.getInstance(), 0, 20);
	}
}
