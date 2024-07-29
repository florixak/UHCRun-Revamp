package me.florixak.uhcrevamp;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.hook.*;
import me.florixak.uhcrevamp.utils.CustomLogFilter;
import me.florixak.uhcrevamp.versions.VersionUtils;
import me.florixak.uhcrevamp.versions.VersionUtils_1_20;
import me.florixak.uhcrevamp.versions.VersionUtils_1_8;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCRevamp extends JavaPlugin {

    private static UHCRevamp plugin;

    public static String nmsVer;
    public static boolean useOldMethods;

    private GameManager gameManager;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().setFilter(new CustomLogFilter());

        getLogger().info(getDescription().getName());
        getLogger().info("Author: " + getAuthor());
        getLogger().info("Web: www.github.com/florixak");
        getLogger().info("Version: " + getDescription().getVersion());

        checkNMSVersion();
        ProtocolLibHook.setupProtocolLib();

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

    public static UHCRevamp getInstance() {
        return plugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    private void registerDependency() {
        VaultHook.setupEconomy();
        LuckPermsHook.setupLuckPerms();
        PAPIHook.setupPlaceholderAPI();
        HeadDatabaseHook.setupHeadDatabase();
    }

    public void checkNMSVersion() {
        nmsVer = Bukkit.getServer().getClass().getPackage().getName();
        nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);

        if (nmsVer.contains("v1_8") || nmsVer.startsWith("v1_7_")) {
            useOldMethods = true;
        }
    }

    public VersionUtils getVersionUtils() {
        if (UHCRevamp.useOldMethods) {
            return new VersionUtils_1_8();
        } else {
            return new VersionUtils_1_20();
        }
    }

}