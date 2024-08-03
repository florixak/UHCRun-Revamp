package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class DeathmatchManager {

	private final GameManager gameManager;
	private final FileConfiguration config;
	private final String path;

	private final String deathmatchWorld;

	public DeathmatchManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

		this.path = "settings.deathmatch";
		this.deathmatchWorld = config.getString(path + ".location.world", "UHCWorld");
	}

	public void checkDeathmatch() {
		if (deathmatchWorld.equals("UHCWorld")) return;
		if (!gameManager.getWorldManager().worldExists(deathmatchWorld)) {
			gameManager.getWorldManager().loadWorldIfExists(deathmatchWorld);

			if (getDeathmatchLocation() != null) {
				Bukkit.getLogger().info("World " + deathmatchWorld + " is loaded.");
			}
		}
	}

	public Location getDeathmatchLocation() {
		return new Location(
				Bukkit.getWorld(config.getString(path + ".location.world", "UHCWorld")),
				config.getDouble(path + ".location.x", 0.0),
				config.getDouble(path + ".location.y", 75.0),
				config.getDouble(path + ".location.z", 0.0));
	}

	public void setDeathmatchLocation(final Location location) {
		config.set(path + ".location.world", location.getWorld().getName());
		config.set(path + ".location.x", location.getX());
		config.set(path + ".location.y", location.getY());
		config.set(path + ".location.z", location.getZ());
		gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public void resetDeathmatchLocation() {
		config.set(path + ".location.world", "world");
		config.set(path + ".location.x", 0.0);
		config.set(path + ".location.y", 75.0);
		config.set(path + ".location.z", 0.0);
		gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
	}

	public double getDeathmatchBorderSize() {
		return config.getDouble(path + ".border-size", 40.0);
	}

	public boolean isDeathmatchEnabled() {
		return config.getBoolean(path + ".enabled", true);
	}

	public Location getTeleportLocation() {
		final Location loc = getDeathmatchLocation();

		return new Location(loc.getWorld(),
				(loc.getX() + (int) (Math.random() * ((getDeathmatchBorderSize() - 1) - loc.getX() + 5))) / 2.2,
				loc.getWorld().getHighestBlockYAt((int) loc.getX(), (int) loc.getZ()),
				(loc.getZ() + (int) (Math.random() * ((getDeathmatchBorderSize() - 1) - loc.getZ() + 5))) / 2.2
		);
	}

	public void prepareDeathmatch() {
		gameManager.getBorderManager().setSize(getDeathmatchBorderSize());

		if (GameValues.TEAM.TEAM_MODE) {
			for (final UHCTeam team : gameManager.getTeamManager().getLivingTeams()) {
				team.teleport(getTeleportLocation());
			}
		} else {
			for (final UHCPlayer uhcPlayer : gameManager.getPlayerManager().getAlivePlayers()) {
				uhcPlayer.teleport(getTeleportLocation());
			}
		}

		if (GameValues.GAME.RESISTANCE_COUNTDOWN > 0) {
			gameManager.getTaskManager().startResistanceTask();
		}
	}
}