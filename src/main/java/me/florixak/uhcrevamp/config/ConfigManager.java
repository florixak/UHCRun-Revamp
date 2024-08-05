package me.florixak.uhcrevamp.config;

import me.florixak.uhcrevamp.UHCRevamp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

	private final Map<ConfigType, ConfigHandler> configurations;

	public ConfigManager() {
		configurations = new HashMap<>();
	}

	public void loadFiles(final UHCRevamp plugin) {

		registerFile(ConfigType.SETTINGS, new ConfigHandler(plugin, "config"));
		registerFile(ConfigType.MESSAGES, new ConfigHandler(plugin, "messages"));
		registerFile(ConfigType.SCOREBOARD, new ConfigHandler(plugin, "scoreboard"));
		registerFile(ConfigType.PLAYER_DATA, new ConfigHandler(plugin, "player-data"));
		registerFile(ConfigType.TEAMS, new ConfigHandler(plugin, "teams"));
		registerFile(ConfigType.KITS, new ConfigHandler(plugin, "kits"));
		registerFile(ConfigType.PERKS, new ConfigHandler(plugin, "perks"));
		registerFile(ConfigType.CUSTOM_DROPS, new ConfigHandler(plugin, "custom-drops"));
		registerFile(ConfigType.CUSTOM_RECIPES, new ConfigHandler(plugin, "custom-recipes"));
		registerFile(ConfigType.ORE_GENERATION, new ConfigHandler(plugin, "ore-generation"));
		registerFile(ConfigType.QUESTS, new ConfigHandler(plugin, "quests"));

		configurations.values().forEach(ConfigHandler::saveDefaultConfig);

		Messages.setConfiguration(getFile(ConfigType.MESSAGES).getConfig());
	}

	public ConfigHandler getFile(final ConfigType type) {
		return configurations.get(type);
	}

	public void reloadFiles() {
		configurations.values().forEach(ConfigHandler::reload);
		Messages.setConfiguration(getFile(ConfigType.MESSAGES).getConfig());
	}

	public void registerFile(final ConfigType type, final ConfigHandler config) {
		configurations.put(type, config);
	}

	public FileConfiguration getFileConfiguration(final File file) {
		return YamlConfiguration.loadConfiguration(file);
	}

	public void saveFile(final ConfigType type) {
		getFile(type).save();
	}

}
