package me.florixak.uhcrun;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
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

    public static String nmsVer;
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

        checkNMSVersion();

        this.gameManager = new GameManager(this);

        registerDependency();
        getGameManager().loadNewGame();
    }

    @Override
    public void onDisable() {
        getGameManager().onDisable();
    }

    public String getAuthor() {
        return getDescription().getAuthors().get(0);
    }

    public static UHCRun getInstance() {
        return plugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    private void registerDependency() {
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        if (config.getBoolean("settings.addons.use-Vault", true)) {
            if (!setupVault()) {
                getLogger().info(TextUtils.color("&cVault plugin not found."));
            }
        }

        if (config.getBoolean("settings.addons.use-LuckPerms", false)) {
            if (!setupLuckPerms()) {
                getLogger().info(TextUtils.color("&cLuckPerms plugin not found."));
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

    public void checkNMSVersion() {
        nmsVer = Bukkit.getServer().getClass().getPackage().getName();
        nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);

        if (nmsVer.equalsIgnoreCase("v1_8_R1") || nmsVer.startsWith("v1_7_")) {
            useOldMethods = true;
        }
    }

}