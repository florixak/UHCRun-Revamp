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

	public EntityListener(GameManager gameManager) {
		this.gameManager = gameManager;
		this.playerManager = gameManager.getPlayerManager();
	}

	@EventHandler
	public void handleEntityDrop(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Animals)) return;

		if (gameManager.getCustomDropManager().hasMobCustomDrop(event.getEntityType())) {
			CustomDrop customDrop = gameManager.getCustomDropManager().getCustomMobDrop(event.getEntityType());
			customDrop.dropMobItem(event);
		}
	}

	@EventHandler
	public void handleDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		if (!gameManager.isPlaying() || gameManager.isEnding() || gameManager.isResistance()) {
			event.setCancelled(true);
			return;
		}

		EntityDamageEvent.DamageCause cause = event.getCause();

		if (gameManager.getGameState() == GameState.MINING) {
			if (gameManager.getCurrentCountdown() >= (GameValues.GAME.MINING_COUNTDOWN - 60)) {
				if (cause.equals(EntityDamageEvent.DamageCause.FALL)) {
					event.setCancelled(true);
				}
			}
			List<String> disabledCauses = GameValues.GAME.DISABLED_IN_MINING;
			if (!disabledCauses.isEmpty()) {
				for (String causeName : disabledCauses) {
					if (cause.name().equalsIgnoreCase(causeName)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void handleEntityHitEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			if (!gameManager.isPlaying()) {
				event.setCancelled(true);
			}
			return;
		}

		Player damager = (Player) event.getDamager();
		UHCPlayer uhcPlayerD = playerManager.getUHCPlayer(damager.getUniqueId());

		if (!gameManager.isPlaying() || uhcPlayerD.isDead() || gameManager.isEnding()) {
			event.setCancelled(true);
			return;
		}

		if (gameManager.getGameState().equals(GameState.MINING) || gameManager.isResistance()) {
			if (!(event.getEntity() instanceof Player)) return;
			event.setCancelled(true);
			return;
		}

		if (event.getEntity() instanceof Player) {
			Player entity = (Player) event.getEntity();
			UHCPlayer uhcPlayerE = playerManager.getUHCPlayer(entity.getUniqueId());

			if (GameValues.TEAM.TEAM_MODE && uhcPlayerD.getTeam() == uhcPlayerE.getTeam() && !GameValues.TEAM.FRIENDLY_FIRE) {
				event.setCancelled(true);
				uhcPlayerD.sendMessage(Messages.TEAM_NO_FRIENDLY_FIRE.toString());
			}
		}
	}

	@EventHandler
	public void handleHealthRegain(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();

		if (reason.equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleProjectileHit(EntityDamageByEntityEvent event) {
		if (!gameManager.isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (!GameValues.GAME.PROJECTILE_HIT_HP_ENABLED) return;
		if (!(event.getEntity() instanceof Player)) return;

		if (event.getDamager() instanceof Snowball) {
			Snowball snowball = (Snowball) event.getDamager();
			if (!(snowball.getShooter() instanceof Player)) return;

			Player shooter = (Player) snowball.getShooter();
			Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);

		} else if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (!(arrow.getShooter() instanceof Player)) return;

			Player shooter = (Player) arrow.getShooter();
			Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);
		} else if (event.getDamager() instanceof FishHook) {
			FishHook fishHook = (FishHook) event.getDamager();
			if (!(fishHook.getShooter() instanceof Player)) return;

			Player shooter = (Player) fishHook.getShooter();
			Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);
		} else if (event.getDamager() instanceof Egg) {
			Egg egg = (Egg) event.getDamager();
			if (!(egg.getShooter() instanceof Player)) return;

			Player shooter = (Player) egg.getShooter();
			Player enemy = (Player) event.getEntity();
			handleProjectileHit(shooter, enemy);
		}
	}

	private void handleProjectileHit(Player shooter, Player enemy) {
		shooter.sendMessage(Messages.SHOT_HP.toString().replace("%player%", enemy.getDisplayName()).replace("%hp%", String.valueOf(enemy.getHealth())));
	}

	@EventHandler
	public void handleMonsterTargeting(EntityTargetEvent event) {
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
	public void handleMonsterSpawning(CreatureSpawnEvent event) {
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
	public void handleExplode(EntityExplodeEvent event) {
		if (!gameManager.isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (GameValues.GAME.EXPLOSIONS_DISABLED) event.setCancelled(true);
	}
}
