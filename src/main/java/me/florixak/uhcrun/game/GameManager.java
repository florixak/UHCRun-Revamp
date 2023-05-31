package me.florixak.uhcrun.game;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.commands.*;
import me.florixak.uhcrun.config.ConfigManager;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.customDrop.CustomDropManager;
import me.florixak.uhcrun.game.deathchest.DeathChestManager;
import me.florixak.uhcrun.manager.DeathmatchManager;
import me.florixak.uhcrun.listener.*;
import me.florixak.uhcrun.listener.events.GameEndEvent;
import me.florixak.uhcrun.manager.gui.GuiManager;
import me.florixak.uhcrun.game.kits.KitsManager;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.manager.lobby.LobbyManager;
import me.florixak.uhcrun.game.perks.PerksManager;
import me.florixak.uhcrun.game.oreGen.OreGenManager;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.manager.scoreboard.ScoreboardManager;
import me.florixak.uhcrun.sql.MySQL;
import me.florixak.uhcrun.sql.SQLGetter;
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

public class GameManager {

    private UHCRun plugin;
    private FileConfiguration config;

    private static GameManager gameManager;
    private GameState gameState;

    private MySQL mysql;
    private SQLGetter data;

    private boolean forceStarted;
    private boolean pvp;

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
    private DeathmatchManager deathmatchManager;
    private OreGenManager oreGenManager;
    private WorldManager worldManager;

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
        this.deathChestManager = new DeathChestManager(this);
        this.deathmatchManager = new DeathmatchManager(this);
        this.oreGenManager = new OreGenManager(this);
        this.soundManager = new SoundManager();
        this.recipeManager = new RecipeManager();
        this.worldManager = new WorldManager();

        this.utils = new Utils(this);
        this.teleportUtils = new TeleportUtils(this);

        this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public void loadNewGame() {
        setGameState(GameState.LOBBY);

        registerCommands();
        registerListeners();

        setForceStarted(false);
        setPvP(false);

        connectToDatabase();

//        getWorldManager().createLobbyWorld("uhc-world");
        getWorldManager().createLobbyWorld("wait-lobby");
        getWorldManager().createLobbyWorld("end-lobby");

        getBorderManager().setBorder();
        getOreGenManager().generateOres();

        getRecipeManager().registerRecipes();
        getCustomDropManager().loadCustomDrops();
        getTeamManager().loadTeams();
        getKitsManager().loadKits();
        // getPerksManager().loadPerks();
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
                Utils.broadcast(Messages.GAME_STARTING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(StartingCD.countdown)));
                break;

            case MINING:
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameStarted(player));

                getPlayerManager().getPlayers().forEach(getPlayerManager()::readyPlayerForGame);
                getTeamManager().getTeams().forEach(uhcTeam -> uhcTeam.teleport(TeleportUtils.getSafeLocation()));

                getTaskManager().startMiningCD();
                Utils.broadcast(Messages.MINING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(MiningCD.countdown)));
                break;

            case FIGHTING:

                if (isTeleportAfterMining()) {
                    getTeamManager().teleportAfterMining();
                }
                setPvP(true);
                getTaskManager().startFightingCD();

                Utils.broadcast(Messages.PVP.toString());
                Utils.broadcast(Messages.BORDER_SHRINK.toString());
                break;

            case DEATHMATCH:
                getDeathmatchManager().prepareDeathmatch();
                getTaskManager().startDeathmatchCD();

                Utils.broadcast(Messages.DEATHMATCH.toString());
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playDMBegan(player));
                break;

            case ENDING:
                Utils.broadcast(Messages.GAME_ENDED.toString());
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameEnd(player));

                setUHCWinner();
                plugin.getServer().getPluginManager().callEvent(new GameEndEvent(getUHCWinner()));

                getTaskManager().startEndingCD();

                getPlayerManager().getPlayers().stream().filter(uhcPlayer -> uhcPlayer.isOnline())
                        .forEach(uhcPlayer -> getPlayerManager().teleport(uhcPlayer.getPlayer()));
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

    public boolean isPlaying() {
        return gameState.equals(GameState.MINING)
                || gameState.equals(GameState.FIGHTING)
                || gameState.equals(GameState.DEATHMATCH)
                || gameState.equals(GameState.ENDING);
    }

    public boolean isForceStarted() {
        return this.forceStarted;
    }
    public void setForceStarted(boolean b) {
        this.forceStarted = b;
    }
    public boolean isPvP() {
        return this.pvp;
    }
    public void setPvP(boolean b) {
        this.pvp = b;
    }

    public World getGameWorld() {
        return Bukkit.getWorld("world");
    }
    public boolean isTeamMode() {
        return config.getBoolean("settings.teams.team-mode", true);
    }
    public boolean isTeleportAfterMining() {
        return config.getBoolean("settings.game.teleport-after-mining", true);
    }
    public boolean areKitsEnabled() {
        return config.getBoolean("settings.kits.enabled", true);
    }
    public boolean arePerksEnabled() {
        return config.getBoolean("settings.perks.enabled", true);
    }
    public boolean areCustomDropsEnabled() {
        return config.getBoolean("settings.game.custom-drops", true);
    }
    public boolean areStatisticsAddedOnEnd() {
        return config.getBoolean("settings.statistics.add-up-game-ends", false);
    }
    public boolean isDeathChestEnabled() {
        return config.getBoolean("settings.death-chest.enabled", true);
    }
    public boolean areExplosionsEnabled() {
        return !config.getBoolean("settings.game.no-explosions", true);
    }
    public boolean isRandomDrop() {
        return config.getBoolean("settings.game.random-drops", false);
    }
    public boolean isNetherAllowed() {
        return config.getBoolean("settings.game.allow-nether", false);
    }

    public MySQL getSQL() {
        return this.mysql;
    }
    public SQLGetter getData() {
        return this.data;
    }
    private void connectToDatabase() {
        String path = "settings.MySQL";
        if (!config.getBoolean( path + ".enabled", false)) return;

        this.mysql = new MySQL(
                config.getString(path + ".host", "localhost"),
                config.getString(path + ".port", "3306"),
                config.getString(path + ".database", "uhcrun"),
                config.getString(path + ".username", "root"),
                config.getString(path + ".password", "")
        );
        this.data = new SQLGetter(this);
        this.data.createTable();
    }
    private void disconnectDatabase() {
        if (config.getBoolean("settings.MySQL.enabled", false)) {
            mysql.disconnect();
        }
    }

    private void registerListeners() {
        List<Listener> listeners = new ArrayList<>();

        listeners.add(new GameListener(gameManager));
        listeners.add(new PlayerListener(gameManager));
        listeners.add(new ChatListener(gameManager));
        listeners.add(new InteractListener(gameManager));
        listeners.add(new InventoryClickListener(gameManager));

        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
    private void registerCommands() {
        registerCommand("uhcrun", new SetupCommand(gameManager));
        registerCommand("forcestart", new ForceStartCommand(gameManager));
        registerCommand("team", new TeamCommand(gameManager));
        registerCommand("workbench", new WorkbenchCommand(gameManager));
        registerCommand("anvil", new AnvilCommand(gameManager));
        registerCommand("nick", new NicknameCommand(playerManager));
        registerCommand("unnick", new NicknameCommand(playerManager));
        registerCommand("kits", new KitsCommand(gameManager));
        registerCommand("statistics", new StatisticsCommand(gameManager));
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

        if (getPlayerManager().getAlivePlayers().isEmpty()
                || getPlayerManager().getAlivePlayers().size() == 0) return;

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
            TeamManager tM = getTeamManager();
            return tM.getWinnerTeam() != null ? (tM.getWinnerTeam().getMembers().size() == 1 ? tM.getWinnerTeam().getMembers().get(0).getName()
                                                                                             : tM.getWinnerTeam().getName())
                                              : "None";
        }
        return getPlayerManager().getWinnerPlayer() != null ? getPlayerManager().getWinnerPlayer().getName() : "None";
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
    public DeathmatchManager getDeathmatchManager() {
        return deathmatchManager;
    }
    public OreGenManager getOreGenManager() {
        return oreGenManager;
    }
    public WorldManager getWorldManager() {
        return worldManager;
    }

    public Utils getUtils() {
        return utils;
    }
    public TeleportUtils getTeleportUtils() {
        return teleportUtils;
    }

    public boolean isVaultEnabled() {
        return config.getBoolean("settings.addons.use-Vault", false) && UHCRun.getVault() != null;
    }
    public boolean areLuckPermsEnabled() {
        return config.getBoolean("settings.addons.use-LuckPerms", false) && UHCRun.getLuckPerms() != null;
    }
    public boolean isProtocolLibEnabled() {
        return config.getBoolean("settings.addons.use-ProtocolLib");
    }
}