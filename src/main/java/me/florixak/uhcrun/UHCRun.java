package me.florixak.uhcrun;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.hook.LuckPermsHook;
import me.florixak.uhcrun.hook.PAPIHook;
import me.florixak.uhcrun.hook.ProtocolLibHook;
import me.florixak.uhcrun.hook.VaultHook;
import me.florixak.uhcrun.manager.WorldManager;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCRun extends JavaPlugin {

    private static UHCRun plugin;

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
            VaultHook.setupEconomy();
            if (!VaultHook.hasEconomy()) getLogger().info(TextUtils.color("&cVault plugin not found."));
        }

        if (config.getBoolean("settings.addons.use-LuckPerms", false)) {
            LuckPermsHook.setupLuckPerms();
            if (!LuckPermsHook.hasLuckPerms()) getLogger().info(TextUtils.color("&cLuckPerms plugin not found."));
        }

        if (config.getBoolean("settings.addons.use-PlaceholderAPI", false)) {
            PAPIHook.setupPlaceholderAPI();
        }

        if (config.getBoolean("settings.addons.use-ProtocolLib", false)) {
            ProtocolLibHook.setupProtocolLib();
        }
    }

    public void checkNMSVersion() {
        nmsVer = Bukkit.getServer().getClass().getPackage().getName();
        nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);

        if (nmsVer.equalsIgnoreCase("v1_8_R1") || nmsVer.startsWith("v1_7_")) {
            useOldMethods = true;
        }
    }

}