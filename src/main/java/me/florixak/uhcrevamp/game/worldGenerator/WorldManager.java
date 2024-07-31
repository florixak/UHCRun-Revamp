package me.florixak.uhcrevamp.game.worldGenerator;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameValues;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

public class WorldManager {

	public void createNewUHCWorld() {
		removeWorld();

		WorldCreator worldCreator = new WorldCreator(GameValues.WORLD_NAME).environment(World.Environment.NORMAL);
		World world = Bukkit.createWorld(worldCreator);
		if (UHCRevamp.useOldMethods) {
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setGameRuleValue("announceAdvancements", "false");
			world.setGameRuleValue("doWeatherCycle", "false");
		} else {
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
			world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		}

	}

	private void removeWorld() {
		try {
			File world = new File(Bukkit.getWorldContainer(), GameValues.WORLD_NAME);
			FileUtils.deleteDirectory(world);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createLobbyWorld(String lobbyName) {
		WorldCreator c = new WorldCreator(lobbyName);
		c.type(WorldType.FLAT);
		World world = c.createWorld();
	}

	public boolean doesWorldExist(String worldName) {
		File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
		return worldFolder.exists() && worldFolder.isDirectory();
	}

	public void loadWorldIfExists(String worldName) {
		if (doesWorldExist(worldName) && Bukkit.getWorld(worldName) == null) {
			WorldCreator creator = new WorldCreator(worldName);
			Bukkit.createWorld(creator);
		} else {
			createLobbyWorld(worldName);
		}
	}
}
