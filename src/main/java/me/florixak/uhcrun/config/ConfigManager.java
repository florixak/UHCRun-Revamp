package me.florixak.uhcrun.config;

import me.florixak.uhcrun.UHCRun;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private Map<ConfigType, ConfigHandler> configurations;

    public ConfigManager() {
        configurations = new HashMap<>();
    }

    public void loadFiles(UHCRun plugin) {

        registerFile(ConfigType.SETTINGS, new ConfigHandler(plugin, "config"));
        registerFile(ConfigType.MESSAGES, new ConfigHandler(plugin, "messages"));
        registerFile(ConfigType.SCOREBOARD, new ConfigHandler(plugin, "scoreboard"));
        registerFile(ConfigType.PLAYER_DATA, new ConfigHandler(plugin, "player-data"));
        registerFile(ConfigType.TEAMS, new ConfigHandler(plugin, "teams"));
        registerFile(ConfigType.KITS, new ConfigHandler(plugin, "kits"));
        registerFile(ConfigType.PERKS, new ConfigHandler(plugin, "perks"));
        registerFile(ConfigType.LOBBY, new ConfigHandler(plugin, "lobby"));
        registerFile(ConfigType.CUSTOM_DROPS, new ConfigHandler(plugin, "custom-drops"));
        registerFile(ConfigType.ORE_GENERATION, new ConfigHandler(plugin, "ore-generation"));

        configurations.values().forEach(ConfigHandler::saveDefaultConfig);

        Messages.setConfiguration(getFile(ConfigType.MESSAGES).getConfig());
    }

    public ConfigHandler getFile(ConfigType type) {
        return configurations.get(type);
    }

    public void reloadFiles() {
        configurations.values().forEach(ConfigHandler::reload);
        Messages.setConfiguration(getFile(ConfigType.MESSAGES).getConfig());
    }

    public void registerFile(ConfigType type, ConfigHandler config) {
        configurations.put(type, config);
    }

    public FileConfiguration getFileConfiguration(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

}
