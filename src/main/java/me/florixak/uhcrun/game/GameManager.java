package me.florixak.uhcrun.game;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.commands.AnvilCommand;
import me.florixak.uhcrun.commands.NicknameCommand;
import me.florixak.uhcrun.commands.WorkbenchCommand;
import me.florixak.uhcrun.config.ConfigManager;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.events.GameEndEvent;
import me.florixak.uhcrun.kits.KitsManager;
import me.florixak.uhcrun.listener.ChatListener;
import me.florixak.uhcrun.listener.GameListener;
import me.florixak.uhcrun.listener.InteractListener;
import me.florixak.uhcrun.listener.PlayerListener;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.perks.PerksManager;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.scoreboard.ScoreboardManager;
import me.florixak.uhcrun.tasks.*;
import me.florixak.uhcrun.teams.TeamManager;
import me.florixak.uhcrun.utils.*;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private UHCRun plugin;

    private static GameManager gameManager;
    private GameState gameState;

    private FileConfiguration config, messages;

    private boolean teamMode;
    private boolean forceStarted;

    private ConfigManager configManager;
    private PlayerManager playerManager;
    private ScoreboardManager scoreboardManager;
    private TabManager tabManager;
    private LocationManager lobbyManager;
    private BorderManager borderManager;
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

    public GameManager(UHCRun plugin){
        this.plugin = plugin;
        gameManager = this;

        this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages = getConfigManager().getFile(ConfigType.MESSAGES).getConfig();

        this.configManager = new ConfigManager();
        this.configManager.loadFiles(plugin);

        this.recipeManager = new RecipeManager();
        this.playerManager = new PlayerManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.tabManager = new TabManager();
        this.lobbyManager = new LocationManager(this);
        this.borderManager = new BorderManager(this);
        this.statisticManager = new StatisticsManager(this);
        this.levelManager = new LevelManager(this);
        this.kitsManager = new KitsManager(this);
        this.perksManager = new PerksManager(this);
        this.teamManager = new TeamManager(this);
        this.taskManager = new TaskManager(this);
        this.soundManager = new SoundManager();

        this.utilities = new Utils(this);
        this.teleportUtil = new TeleportUtils(this);

        getBorderManager().checkBorder();

        setOreSpawn();

        getTaskManager().runActivityRewards();
        getTaskManager().runAutoBroadcast();
    }

    private void loadNewGame() {
        setGameState(GameState.LOBBY);

        registerCommands();
        registerListeners();

        this.forceStarted = false;

        getRecipeManager().registerRecipes();

        getTaskManager().runScoreboardUpdate();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState){
        if (this.gameState == gameState) return;
        this.gameState = gameState;

        switch (gameState) {

            case LOBBY:
                getTaskManager().stopStartingCD();
                break;

            case STARTING:
                getTaskManager().startStartingCD();
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playStartingSound(player));
                Utils.broadcast(Messages.GAME_STARTING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(StartingCd.count)));
                break;

            case MINING:
                getPlayerManager().getPlayers().forEach(getPlayerManager()::readyPlayerForGame);
                getPlayerManager().getPlayers().forEach(getKitsManager()::giveKit);
                getTaskManager().startMiningCD();
                Utils.broadcast(Messages.MINING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(MiningCd.count)));
                break;

            case FIGHTING:
                getPlayerManager().getPlayers().forEach(player -> getPlayerManager().teleportPlayersAfterMining(player));
                getTaskManager().startFightingCD();
                Utils.broadcast(Messages.PVP.toString());
                Utils.broadcast(Messages.BORDER_SHRINK.toString());
                break;

            case DEATHMATCH:
                getTaskManager().startDeathmatchCD();
                Utils.broadcast(Messages.DEATHMATCH_STARTED.toString());
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playDMBegan(player));
                break;

            case ENDING:
                getTaskManager().startEndingCD();
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameEnd(player));
                Utils.broadcast(Messages.GAME_ENDED.toString());
                plugin.getServer().getPluginManager().callEvent(new GameEndEvent(getPlayerManager().getUHCWinner()));
                break;
        }
    }

    public void onDisable() {

        getPlayerManager().clearPlayers();
        getTeamManager().onDisable();
        getTaskManager().onDisable();
    }

    public boolean isForceStarted() {
        return this.forceStarted;
    }

    public boolean isTeamMode() {
        return config.getBoolean("settings.game.team-mode");
    }

    public boolean isPlaying() {
        return (gameState == GameState.MINING) || (gameState == GameState.FIGHTING) || (gameState == GameState.DEATHMATCH);
    }

    private void registerListeners() {
        List<Listener> listeners = new ArrayList<>();

        listeners.add(new GameListener(gameManager));
        listeners.add(new PlayerListener(gameManager));
        listeners.add(new ChatListener(gameManager));
        listeners.add(new InteractListener(gameManager));

        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private void registerCommands() {
        registerCommand("workbench", new WorkbenchCommand(gameManager));
        registerCommand("anvil", new AnvilCommand(gameManager)); // TODO make anvil command
        registerCommand("nick", new NicknameCommand(playerManager));
        registerCommand("unnick", new NicknameCommand(playerManager));
    }

    private void registerCommand(String commandN, CommandExecutor executor) {
        PluginCommand command = UHCRun.getInstance().getCommand(commandN);

        if (command == null) {
            UHCRun.getInstance().getLogger().info("Error in registering command! (" + command + ")");
            return;
        }
        command.setExecutor(executor);
    }


    public void setOreSpawn() {
        OreUtils oreUtility = new OreUtils();
        World world = Bukkit.getWorld(config.getString("game-world"));
        Random random = new Random();
        int border = config.getInt("border.size");
        Location loc;

        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.DIAMOND_ORE);
            oreUtility.generateVein(Material.DIAMOND_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 600; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.IRON_ORE);
            oreUtility.generateVein(Material.IRON_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 600; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.COAL_ORE);
            oreUtility.generateVein(Material.COAL_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.EMERALD_ORE);
            oreUtility.generateVein(Material.EMERALD_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.GOLD_ORE);
            oreUtility.generateVein(Material.GOLD_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.REDSTONE_ORE);
            oreUtility.generateVein(Material.REDSTONE_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 200; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.OBSIDIAN);
            oreUtility.generateVein(Material.OBSIDIAN, world.getBlockAt(loc), amount);
        }
    }

    public static GameManager getGameManager() {
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
    public TabManager getTabManager() {
        return tabManager;
    }
    public LocationManager getLocationManager() { return lobbyManager; }
    public BorderManager getBorderManager() { return borderManager; }
    public StatisticsManager getStatistics() {
        return statisticManager;
    }
    public LevelManager getLevelManager() {
        return levelManager;
    }
    public TeamManager getTeamManager() {
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
    public TaskManager getTaskManager() {
        return taskManager;
    }
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public Utils getUtilities() {
        return utilities;
    }
    public TeleportUtils getTeleportUtil() {
        return teleportUtil;
    }
}