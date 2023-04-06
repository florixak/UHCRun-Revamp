package me.florixak.uhcrun;

import me.florixak.uhcrun.action.ActionManager;
import me.florixak.uhcrun.commands.AnvilCommand;
import me.florixak.uhcrun.commands.NicknameCommand;
import me.florixak.uhcrun.commands.WorkbenchCommand;
import me.florixak.uhcrun.config.ConfigManager;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.kits.KitsManager;
import me.florixak.uhcrun.listener.ChatListener;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.inventory.InventoryManager;
import me.florixak.uhcrun.listener.InteractListener;
import me.florixak.uhcrun.listener.GameListener;
import me.florixak.uhcrun.listener.PlayerListener;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.perks.PerksManager;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.scoreboard.ScoreboardManager;
import me.florixak.uhcrun.sql.MySQL;
import me.florixak.uhcrun.sql.SQLGetter;
import me.florixak.uhcrun.utils.TeleportUtils;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.placeholderapi.PlaceholderExp;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class UHCRun extends JavaPlugin {

    private static UHCRun plugin;
    private static Economy econ = null;
    private static LuckPerms luckPerms = null;

    public MySQL mysql;
    public SQLGetter data;

    private GameManager gameManager;
    private ConfigManager configManager;
    private PlayerManager playerManager;
    private ScoreboardManager scoreboardManager;
    private LocationManager lobbyManager;
    private BorderManager borderManager;
    private ActionManager actionManager;
    private InventoryManager inventoryManager;
    private StatisticsManager statisticManager;
    private LevelManager levelManager;
    private KitsManager kitsManager;
    private PerksManager perksManager;
    private TaskManager taskManager;
    private TeamManager teamManager;
    private SoundManager soundManager;
    private RecipeManager recipeManager;

    private Utils utilities;
    private TeleportUtils teleportUtil;

    public static UHCRun getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {

        getLogger().info("Author: FloriXak");
        getLogger().info("Web: www.florixak.tk");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("Created 4fun");

        plugin = this;
        this.configManager = new ConfigManager();
        this.configManager.loadFiles(this);
        this.gameManager = new GameManager(this);
        this.playerManager = new PlayerManager();
        this.scoreboardManager = new ScoreboardManager(this);
        this.lobbyManager = new LocationManager(this);
        this.borderManager = new BorderManager(this);
        this.actionManager = new ActionManager(this);
        this.inventoryManager = new InventoryManager();
        this.inventoryManager.onEnable(this);
        this.statisticManager = new StatisticsManager(this);
        this.levelManager = new LevelManager(this);
        this.kitsManager = new KitsManager(this);
        this.perksManager = new PerksManager(this);
        this.teamManager = new TeamManager(this);
        this.taskManager = new TaskManager(this);
        this.soundManager = new SoundManager();
        this.recipeManager = new RecipeManager();

        this.utilities = new Utils(this);
        this.teleportUtil = new TeleportUtils(this);

        registerDependency();
        connectToDatabase();

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        getInventoryManager().onDisable();
        getGame().onDisable();
        disconnectDatabase();
    }

    private void registerListeners() {
        List<Listener> listeners = new ArrayList<>();
        listeners.add(new GameListener(this));
        listeners.add(new PlayerListener(this));
        listeners.add(new ChatListener(this));
        listeners.add(new InteractListener(this));
//        listeners.add(new VanishUtils());

        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerCommands() {
        registerCommand("workbench", new WorkbenchCommand(this));
        registerCommand("anvil", new AnvilCommand(this)); // TODO make anvil command
        registerCommand("nick", new NicknameCommand(playerManager));
        registerCommand("unnick", new NicknameCommand(playerManager));
    }

    private void registerCommand(String commandN, CommandExecutor executor) {
         PluginCommand command = UHCRun.getInstance().getCommand(commandN);

         if (command == null) {
             getLogger().info("Error in registering command! (" + command + ")");
             return;
         }
         command.setExecutor(executor);
    }

    private void connectToDatabase() {
        if (configManager.getFile(ConfigType.SETTINGS).getConfig().getBoolean("MySQL.enabled", true)) {
            mysql = new MySQL("localhost", 3306, null, null, null);
            data = new SQLGetter(plugin);
            data.createTable();
        }
    }
    private void disconnectDatabase() {
        if (configManager.getFile(ConfigType.SETTINGS).getConfig().getBoolean("MySQL.enabled", true)) {
            mysql.disconnect();
        }
    }

    private void registerDependency() {
        if (configManager.getFile(ConfigType.SETTINGS).getConfig().getBoolean("use-Vault", true)) {
            if (!setupVault()) {
                getLogger().info(TextUtils.color("&cNo economy plugin found. Disabling UHCRun."));
                return;
            }
            getLogger().info(TextUtils.color("&aVault plugin found."));

        }

        if (configManager.getFile(ConfigType.SETTINGS).getConfig().getBoolean("use-LuckPerms", true)) {
            if (!setupLuckPerms()) {
                getLogger().info(TextUtils.color("&cLuckPerms plugin not found."));
                return;
            }
            getLogger().info(TextUtils.color("&aLuckPerms plugin found."));
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExp(this).register();
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
    public GameManager getGame() {
        return gameManager;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
    public Utils getUtilities() { return utilities; }
    public LocationManager getLobbyManager() { return lobbyManager; }
    public BorderManager getBorderManager() { return borderManager; }
    public ActionManager getActionManager() {
        return actionManager;
    }
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
    public StatisticsManager getStatistics() {
        return statisticManager;
    }
    public LevelManager getLevelManager() {
        return levelManager;
    }
    public TeamManager getTeams() {
        return teamManager;
    }
    public KitsManager getKitsManager() {
        return kitsManager;
    }
    public PerksManager getPerksManager() {
        return perksManager;
    }
    public SoundManager getSoundManager() {
        return soundManager;
    }
    public TaskManager getTasks() {
        return taskManager;
    }
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}