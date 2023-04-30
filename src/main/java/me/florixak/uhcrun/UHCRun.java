package me.florixak.uhcrun;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.sql.MySQL;
import me.florixak.uhcrun.sql.SQLGetter;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.placeholderapi.PlaceholderExp;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCRun extends JavaPlugin {

    private static UHCRun plugin;
    private static Economy econ = null;
    private static LuckPerms luckPerms = null;

    public static String nmsver;
    public static boolean useOldMethods;

    public MySQL mysql;
    public SQLGetter data;

    private GameManager gameManager;

    @Override
    public void onEnable() {
        plugin = this;

        getLogger().info("Author: FloriXak");
        getLogger().info("Web: www.florixak.tk");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("Created 4fun");

        registerDependency();
        connectToDatabase();

        this.gameManager = new GameManager(plugin);


    }

    @Override
    public void onDisable() {
        getGameManager().onDisable();
        disconnectDatabase();
    }

    public static UHCRun getInstance() {
        return plugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    private void connectToDatabase() {
        if (gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getBoolean("MySQL.enabled", true)) {
            this.mysql = new MySQL("localhost", 3306, null, null, null);
            this.data = new SQLGetter(plugin);
            this.data.createTable();
        }
    }
    private void disconnectDatabase() {
        if (gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getBoolean("MySQL.enabled", true)) {
            mysql.disconnect();
        }
    }

    private void registerDependency() {
        if (gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getBoolean("use-Vault", true)) {
            if (!setupVault()) {
                UHCRun.getInstance().getLogger().info(TextUtils.color("&cNo economy plugin found. Disabling UHCRun."));
                return;
            }
            UHCRun.getInstance().getLogger().info(TextUtils.color("&aVault plugin found."));

        }

        if (gameManager.getConfigManager().getFile(ConfigType.SETTINGS)
                .getConfig().getBoolean("use-LuckPerms", true)) {
            if (!setupLuckPerms()) {
                UHCRun.getInstance().getLogger().info(TextUtils.color("&cLuckPerms plugin not found."));
                return;
            }
            UHCRun.getInstance().getLogger().info(TextUtils.color("&aLuckPerms plugin found."));
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExp(plugin).register();
        }

        if (gameManager.getConfigManager().getFile(ConfigType.SETTINGS)
                .getConfig().getBoolean("settings.addons.use-ProtocolLib", true)
                && Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().info("ProtocolLib plugin not found.");
            getServer().getPluginManager().disablePlugin(this);
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