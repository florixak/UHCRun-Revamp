package me.florixak.uhcrun.game;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.commands.*;
import me.florixak.uhcrun.config.ConfigManager;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.customDrop.CustomDropManager;
import me.florixak.uhcrun.game.deathchest.DeathChestManager;
import me.florixak.uhcrun.listener.events.GameEndEvent;
import me.florixak.uhcrun.manager.gui.GuiManager;
import me.florixak.uhcrun.game.kits.KitsManager;
import me.florixak.uhcrun.listener.ChatListener;
import me.florixak.uhcrun.listener.GameListener;
import me.florixak.uhcrun.listener.InteractListener;
import me.florixak.uhcrun.listener.PlayerListener;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.manager.lobby.LobbyManager;
import me.florixak.uhcrun.game.perks.PerksManager;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.scoreboard.ScoreboardManager;
import me.florixak.uhcrun.sql.MySQL;
import me.florixak.uhcrun.sql.SQLGetter;
import me.florixak.uhcrun.tasks.*;
import me.florixak.uhcrun.teams.TeamManager;
import me.florixak.uhcrun.utils.*;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
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
    private FileConfiguration config;

    private static GameManager gameManager;
    private GameState gameState;

    private MySQL mysql;
    private SQLGetter data;

    private World game_world;
    private boolean teamMode;
    private boolean forceStarted;
    private boolean teleportAfterMining;
    private boolean enableDeathmatch;
    private boolean enableKits;
    private boolean enablePerks;
    private boolean enableCustomDrops;
    private boolean statisticsOnEnd;
    private boolean enableDeathChest;
    private boolean noExplosions;

    private ConfigManager configManager;
    private PlayerManager playerManager;
    private ScoreboardManager scoreboardManager;
    private TabManager tabManager;
    private LobbyManager lobbyManager;
    private BorderManager borderManager;
    private KitsManager kitsManager;
    private PerksManager perksManager;
    private TaskManager taskManager;
    private TeamManager teamManager;
    private GuiManager guiManager;
    private CustomDropManager customDropManager;
    private SoundManager soundManager;
    private RecipeManager recipeManager;
    private DeathChestManager deathChestManager;

    private Utils utils;
    private TeleportUtils teleportUtils;

    public GameManager(UHCRun plugin){
        this.plugin = plugin;
        gameManager = this;

        this.configManager = new ConfigManager();
        this.configManager.loadFiles(plugin);

        this.playerManager = new PlayerManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.tabManager = new TabManager(this);
        this.lobbyManager = new LobbyManager(this);
        this.borderManager = new BorderManager(this);
        this.kitsManager = new KitsManager(this);
        this.perksManager = new PerksManager(this);
        this.teamManager = new TeamManager(this);
        this.guiManager = new GuiManager();
        this.customDropManager = new CustomDropManager(this);
        this.taskManager = new TaskManager(this);
        this.soundManager = new SoundManager();
        this.recipeManager = new RecipeManager();
        this.deathChestManager = new DeathChestManager(this);

        this.utils = new Utils(this);
        this.teleportUtils = new TeleportUtils(this);

        this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public void loadNewGame() {
        setGameState(GameState.LOBBY);

        registerCommands();
        registerListeners();

        this.game_world = Bukkit.getWorld(config.getString("settings.game.game-world"));
        this.forceStarted = false;
        this.teamMode = config.getBoolean("settings.teams.team-mode");
        this.teleportAfterMining = config.getBoolean("settings.game.teleport-after-mining");
        this.enableDeathmatch = config.getBoolean("settings.deathmatch.enabled");
        this.enableKits = config.getBoolean("settings.kits.enabled");
        this.enablePerks = config.getBoolean("settings.perks.enabled");
        this.enableCustomDrops = config.getBoolean("settings.game.custom-drops");
        this.statisticsOnEnd = config.getBoolean("settings.statistics.add-on-game-ends");
        this.enableDeathChest = config.getBoolean("settings.death-chest.enabled");
        this.noExplosions = config.getBoolean("settings.game.no-explosions");

        connectToDatabase();

        getBorderManager().setBorder();
        generateOres();

        getRecipeManager().registerRecipes();
        getCustomDropManager().loadCustomDrops();
        getTeamManager().loadTeams();
        getKitsManager().loadKits();
        getGuiManager().loadInventories();

        getTaskManager().runGameChecking();
        getTaskManager().runScoreboardUpdate();
        getTaskManager().runActivityRewards();
        getTaskManager().runAutoBroadcast();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState){
        if (this.gameState == gameState) return;
        this.gameState = gameState;

        switch (gameState) {

            case LOBBY:
                break;

            case STARTING:
                getTaskManager().startStartingCD();
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playStartingSound(player));
                Utils.broadcast(Messages.GAME_STARTING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(StartingCD.count)));
                break;

            case MINING:
                getPlayerManager().getPlayers().forEach(getPlayerManager()::readyPlayerForGame);
                getPlayerManager().getAlivePlayers().forEach(uhcPlayer -> uhcPlayer.getPlayer().teleport(TeleportUtils.teleportToSafeLocation()));
                getPlayerManager().getAlivePlayers().stream()
                        .filter(uhcPlayer -> uhcPlayer.hasKit())
                        .forEach(uhcPlayer -> uhcPlayer.getKit().getKit(uhcPlayer));
                getTaskManager().startMiningCD();
                Utils.broadcast(Messages.MINING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(MiningCD.count)));
                break;

            case FIGHTING:
                if (isTeleportAfterMining()) {
                    getTeamManager().teleportAfterMining();
                }
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
                setUHCWinner();
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameEnd(player));
                Utils.broadcast(Messages.GAME_ENDED.toString());
                plugin.getServer().getPluginManager().callEvent(new GameEndEvent(getUHCWinner()));
                break;
        }
    }

    public void onDisable() {
        getPlayerManager().onDisable();
        getTeamManager().onDisable();
        getDeathChestManager().onDisable();
        getTaskManager().onDisable();
        disconnectDatabase();
    }

    public World getGameWorld() {
        return this.game_world;
    }
    public boolean isForceStarted() {
        return this.forceStarted;
    }
    public void setForceStarted() {
        this.forceStarted = true;
    }
    public boolean isTeamMode() {
        return this.teamMode;
    }
    public boolean isTeleportAfterMining() {
        return this.teleportAfterMining;
    }
    public boolean isDeathmatchEnabled() {
        return this.enableDeathmatch;
    }
    public boolean areKitsEnabled() {
        return this.enableKits;
    }
    public boolean arePerksEnabled() {
        return this.enablePerks;
    }
    public boolean areCustomDropsEnabled() {
        return enableCustomDrops;
    }
    public boolean areStatisticsAddedOnEnd() {
        return statisticsOnEnd;
    }
    public boolean isDeathChestEnabled() {
        return enableDeathChest;
    }
    public boolean areExplosionsEnabled() {
        return !noExplosions;
    }

    public boolean isPlaying() {
        return (gameState == GameState.MINING) || (gameState == GameState.FIGHTING) || (gameState == GameState.DEATHMATCH);
    }

    private void connectToDatabase() {
        if (config.getBoolean("settings.MySQL.enabled", true)) {
            this.mysql = new MySQL("localhost", 3306, null, null, null);
            this.data = new SQLGetter(this);
            this.data.createTable();
        }
    }
    private void disconnectDatabase() {
        if (config.getBoolean("settings.MySQL.enabled", true)) {
            mysql.disconnect();
        }
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
        registerCommand("uhcrun", new SetupCommand(gameManager));
        registerCommand("forcestart", new ForceStartCommand(gameManager));
        registerCommand("team", new TeamCommand(gameManager));
        registerCommand("workbench", new WorkbenchCommand(gameManager));
        registerCommand("anvil", new AnvilCommand(gameManager)); // TODO make anvil command
        registerCommand("nick", new NicknameCommand(playerManager));
        registerCommand("unnick", new NicknameCommand(playerManager));
        registerCommand("kits", new KitsCommand(gameManager));
    }
    private void registerCommand(String commandN, CommandExecutor executor) {
        PluginCommand command = UHCRun.getInstance().getCommand(commandN);

        if (command == null) {
            UHCRun.getInstance().getLogger().info("Error in registering command! (" + command + ")");
            return;
        }
        command.setExecutor(executor);
    }

    public void setUHCWinner() {

        UHCPlayer winner = getPlayerManager().getAlivePlayers().get(0);
        for (UHCPlayer uhcPlayer : getPlayerManager().getAlivePlayers()) {
            if (uhcPlayer == null) return;
            if (uhcPlayer.getKills() > winner.getKills()) winner = uhcPlayer;
        }

        if (isTeamMode()) {
            for (UHCPlayer teamMember : winner.getTeam().getMembers()) {
                teamMember.setWinner(true);
            }
            return;
        }
        winner.setWinner(true);

    }
    public String getUHCWinner() {
        if (isTeamMode()) {
            return getTeamManager().getWinnerTeam() != null ? getTeamManager().getWinnerTeam().getName() : "None";
        }
        return getPlayerManager().getWinnerPlayer() != null ? getPlayerManager().getWinnerPlayer().getName() : "None";
    }

    public void generateOres() {
        World world = getGameWorld();
        Random random = new Random();
        int border = (int) getBorderManager().getSize();

        OreGeneratorUtils.generateOre(XMaterial.COAL_ORE.parseMaterial(), world, random.nextInt(3)+3, 400, border);
        OreGeneratorUtils.generateOre(XMaterial.IRON_ORE.parseMaterial(), world, random.nextInt(3)+2, 400, border);
        OreGeneratorUtils.generateOre(XMaterial.GOLD_ORE.parseMaterial(), world, random.nextInt(3)+2, 400, border);
        OreGeneratorUtils.generateOre(XMaterial.REDSTONE_ORE.parseMaterial(), world, random.nextInt(3)+2, 400, border);
        OreGeneratorUtils.generateOre(XMaterial.DIAMOND_ORE.parseMaterial(), world, random.nextInt(3)+2, 400, border);
        OreGeneratorUtils.generateOre(XMaterial.EMERALD_ORE.parseMaterial(), world, random.nextInt(3)+2, 400, border);
        OreGeneratorUtils.generateOre(XMaterial.OBSIDIAN.parseMaterial(), world, random.nextInt(3)+2, 300, border);
    }

    public MySQL getSQL() {
        return this.mysql;
    }
    public SQLGetter getData() {
        return this.data;
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
    public LobbyManager getLobbyManager() { return lobbyManager; }
    public BorderManager getBorderManager() { return borderManager; }
    public TeamManager getTeamManager() {
        return teamManager;
    }
    public GuiManager getGuiManager() {
        return guiManager;
    }
    public CustomDropManager getCustomDropManager() {
        return customDropManager;
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
    public DeathChestManager getDeathChestManager() {
        return deathChestManager;
    }

    public Utils getUtils() {
        return utils;
    }
    public TeleportUtils getTeleportUtils() {
        return teleportUtils;
    }
}