package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customDrop.CustomDrop;
import me.florixak.uhcrevamp.game.player.PlayerManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.List;

public class EntityListener implements Listener {

	private final GameManager gameManager;
	private final PlayerManager playerManager;

	public EntityListener(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.playerManager = gameManager.getPlayerManager();
	}

	@EventHandler
	public void handleEntityDrop(final EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Animals)) return;

		if (gameManager.getCustomDropManager().hasMobCustomDrop(event.getEntityType())) {
			final CustomDrop customDrop = gameManager.getCustomDropManager().getCustomMobDrop(event.getEntityType());
			customDrop.dropMobItem(event);
		}
	}

	@EventHandler
	public void handleDamage(final EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		if (!gameManager.isPlaying() || gameManager.isEnding() || gameManager.isResistance()) {
			event.setCancelled(true);
			return;
		}

		final EntityDamageEvent.DamageCause cause = event.getCause();

		if (gameManager.getGameState() == GameState.MINING) {
			if (gameManager.getCurrentCountdown() >= (GameValues.GAME.MINING_COUNTDOWN - 60)) {
				if (cause.equals(EntityDamageEvent.DamageCause.FALL)) {
					event.setCancelled(true);
				}
			}
			final List<String> disabledCauses = GameValues.GAME.DISABLED_IN_MINING;
			if (!disabledCauses.isEmpty()) {
				for (final String causeName : disabledCauses) {
					if (cause.name().equalsIgnoreCase(causeName)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void handleEntityHitEntity(final EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) { // if damager not player
			if (!gameManager.isPlaying()) { // if not playing
				event.setCancelled(true); // disable event
			}
			return;
		}

		final Player damager = (Player) event.getDamager();
		final UHCPlayer uhcPlayerD = playerManager.getUHCPlayer(damager.getUniqueId());

		if (!gameManager.isPlaying() || uhcPlayerD.isDead() || gameManager.isEnding()) { // if not playing, dead or ending
			event.setCancelled(true); // disable event
			return;
		}

		if (gameManager.getGameState().equals(GameState.MINING) || gameManager.isResistance()) { // if mining or resistance
			if (!(event.getEntity() instanceof Player)) return; // if entity not player
			event.setCancelled(true); // disable event
			return;
		}

		if (event.getEntity() instanceof Player) {
			final Player entity = (Player) event.getEntity();
			final UHCPlayer uhcPlayerE = playerManager.getUHCPlayer(entity.getUniqueId());

			if (GameValues.TEAM.TEAM_MODE && uhcPlayerD.getTeam() == uhcPlayerE.getTeam() && !GameValues.TEAM.FRIENDLY_FIRE) {
				event.setCancelled(true);
				uhcPlayerD.sendMessage(Messages.TEAM_NO_FRIENDLY_FIRE.toString());
				return;
			}

			if (gameManager.getDamageTrackerManager().isInTracker(uhcPlayerE)) { // if in tracker
				if (uhcPlayerD.equals(gameManager.getDamageTrackerManager().getAttacker(uhcPlayerE))) { // if attacker
					uhcPlayerE.sendMessage("Damager is still the same."); // send message
					return;
				}
				uhcPlayerE.sendMessage("New Damager! You have been damaged by " + uhcPlayerD.getName() + "."); // send message
				final UHCPlayer attacker = gameManager.getDamageTrackerManager().getAttacker(uhcPlayerE); // get attackerage
				gameManager.getDamageTrackerManager().removeFromTracker(uhcPlayerE); // remove from tracker
				gameManager.getDamageTrackerManager().addTrackerOn(uhcPlayerE, uhcPlayerD); // add tracker
				if (gameManager.getDamageTrackerManager().isInAssist(uhcPlayerE)) { // if in assist
					gameManager.getDamageTrackerManager().removeFromAssist(uhcPlayerE); // remove from assist
				}
				uhcPlayerE.sendMessage("Old Damager " + attacker.getName() + " is now assistant!");
				gameManager.getDamageTrackerManager().addAssist(uhcPlayerE, attacker); // add assist
				uhcPlayerE.sendMessage("Test of assistant: " + gameManager.getDamageTrackerManager().getAssistant(uhcPlayerE).getName());
			} else {
				uhcPlayerE.sendMessage("You have been damaged by " + uhcPlayerD.getName() + "."); // send message
				gameManager.getDamageTrackerManager().addTrackerOn(uhcPlayerE, uhcPlayerD); // add tracker
			}
		}
	}

	@EventHandler
	public void handleHealthRegain(final EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		final EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();

		if (reason.equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleProjectileHit(final EntityDamageByEntityEvent event) {
		if (!gameManager.isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (!GameValues.GAME.PROJECTILE_HIT_HP_ENABLED) return;
		if (!(event.getEntity() instanceof Player)) return;

		if (event.getDamager() instanceof Snowball) {
			final Snowball snowball = (Snowball) event.getDamager();
			if (!(snowball.getShooter() instanceof Player)) return;

			final Player shooter = (Player) snowball.getShooter();
			final Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);

		} else if (event.getDamager() instanceof Arrow) {
			final Arrow arrow = (Arrow) event.getDamager();
			if (!(arrow.getShooter() instanceof Player)) return;

			final Player shooter = (Player) arrow.getShooter();
			final Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);
		} else if (event.getDamager() instanceof FishHook) {
			final FishHook fishHook = (FishHook) event.getDamager();
			if (!(fishHook.getShooter() instanceof Player)) return;

			final Player shooter = (Player) fishHook.getShooter();
			final Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);
		} else if (event.getDamager() instanceof Egg) {
			final Egg egg = (Egg) event.getDamager();
			if (!(egg.getShooter() instanceof Player)) return;

			final Player shooter = (Player) egg.getShooter();
			final Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);
		}
	}

	private void handleProjectileHit(final Player shooter, final Player enemy) {
		shooter.sendMessage(Messages.SHOT_HP.toString().replace("%player%", enemy.getDisplayName()).replace("%hp%", String.valueOf(enemy.getHealth())));
	}

	@EventHandler
	public void handleMonsterTargeting(final EntityTargetEvent event) {
		if (!gameManager.isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (event.getEntity() instanceof Monster || event.getEntity() instanceof Slime) {
			if (GameValues.GAME.MONSTERS_ATTACK) return;
			if (event.getTarget() instanceof Player) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void handleMonsterSpawning(final CreatureSpawnEvent event) {
		if (!gameManager.isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (event.getEntity() instanceof Monster || event.getEntity() instanceof Slime) {
			if (GameValues.GAME.SPAWN_MONSTERS) return;
			if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void handleExplode(final EntityExplodeEvent event) {
		if (!gameManager.isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (GameValues.GAME.EXPLOSIONS_DISABLED) event.setCancelled(true);
	}
}
