package me.florixak.uhcrun;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.WorldManager;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.placeholderapi.PlaceholderExp;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCRun extends JavaPlugin {

    private static UHCRun plugin;
    private static Economy econ = null;
    private static LuckPerms luckPerms = null;

    public static String nmsver;
    public static boolean useOldMethods;

    private GameManager gameManager;

    @Override
    public void onLoad() {
        new WorldManager().createNewUHCWorld();
    }

    @Override
    public void onEnable() {
        plugin = this;

        getLogger().info(getDescription().getName());
        getLogger().info("Author: " + getAuthor());
        getLogger().info("Web: www.florixak.tk");
        getLogger().info("Version: " + getDescription().getVersion());

        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

        if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.startsWith("v1_7_")) {
            useOldMethods = true;
        }

        this.gameManager = new GameManager(plugin);

        registerDependency();
        this.gameManager.loadNewGame();
    }

    @Override
    public void onDisable() {
        getGameManager().onDisable();
    }

    public static UHCRun getInstance() {
        return plugin;
    }

    public String getAuthor() {
        return "FloriXak";
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    private void registerDependency() {
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        if (config.getBoolean("settings.addons.use-Vault", true)) {
            if (!setupVault()) {
                getLogger().info(TextUtils.color("&cVault plugin not found."));
                return;
            }
        }

        if (config.getBoolean("settings.addons.use-LuckPerms", false)) {
            if (!setupLuckPerms()) {
                getLogger().info(TextUtils.color("&cLuckPerms plugin not found."));
                return;
            }
        }

        if (config.getBoolean("settings.addons.use-PlaceholderAPI", false)) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null){
                getLogger().info(TextUtils.color("&cPlaceholderAPI plugin not found."));
                return;
            }
            new PlaceholderExp(plugin).register();
        }

        if (config.getBoolean("settings.addons.use-ProtocolLib", false)) {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
                getLogger().info(TextUtils.color("&cProtocolLib plugin not found."));
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    private boolean setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private boolean setupLuckPerms() {
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if (plugin.getName().contains("LuckPerms")) {
                return true;
            }
        }

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            return false;
        }
        luckPerms = provider.getProvider();
        return luckPerms != null;
    }

    public static Economy getVault() {
        return econ;
    }
    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

}