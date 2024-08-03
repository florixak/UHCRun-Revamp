package me.florixak.uhcrevamp.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

	private final JavaPlugin plugin;
	private final String name;
	private final File file;
	private FileConfiguration configuration;

	public ConfigHandler(final JavaPlugin plugin, final String name) {
		this.plugin = plugin;
		this.name = name + ".yml";
		this.file = new File(plugin.getDataFolder(), this.name);
		this.configuration = new YamlConfiguration();
	}

	public void saveDefaultConfig() {
		if (!file.exists()) {
			plugin.saveResource(name, false);
		}

		try {
			configuration.load(file);
		} catch (final InvalidConfigurationException | IOException e) {
			e.printStackTrace();
			plugin.getLogger().severe("============= CONFIGURATION ERROR =============");
			plugin.getLogger().severe("There was an error loading " + name);
			plugin.getLogger().severe("Please check for any obvious configuration mistakes");
			plugin.getLogger().severe("such as using tabs for spaces or forgetting to end quotes");
			plugin.getLogger().severe("before reporting to the developer. The plugin will now disable..");
			plugin.getLogger().severe("============= CONFIGURATION ERROR =============");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}

	}

	public void save() {
		if (configuration == null || file == null) return;
		try {
			getConfig().save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		configuration = YamlConfiguration.loadConfiguration(file);
	}

	public FileConfiguration getConfig() {
		return configuration;
	}
}
