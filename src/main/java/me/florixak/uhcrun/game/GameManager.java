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
import me.florixak.uhcrun.manager.lobby.LobbyType;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.manager.scoreboard.ScoreboardManager;
import me.florixak.uhcrun.sql.MySQL;
import me.florixak.uhcrun.sql.SQLGetter;
import me.florixak.uhcrun.tasks.*;
import me.florixak.uhcrun.teams.TeamManager;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.*;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.XSeries.XSound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final UHCRun plugin;
    private final FileConfiguration config;

    private static GameManager gameManager;
    private GameState gameState;

    private MySQL mysql;
    private SQLGetter data;

    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final ScoreboardManager scoreboardManager;
    private final TabManager tabManager;
    private final LobbyManager lobbyManager;
    private final BorderManager borderManager;
    private final KitsManager kitsManager;
    private final PerksManager perksManager;
    private final TaskManager taskManager;
    private final TeamManager teamManager;
    private final GuiManager guiManager;
    private final CustomDropManager customDropManager;
    private final SoundManager soundManager;
    private final RecipeManager recipeManager;
    private final DeathChestManager deathChestManager;
    private final DeathmatchManager deathmatchManager;
    private final OreGenManager oreGenManager;
    private final WorldManager worldManager;

    private boolean forceStarted;
    private boolean pvp;

    public GameManager(UHCRun plugin) {
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
        this.guiManager = new GuiManager(this);
        this.customDropManager = new CustomDropManager(this);
        this.taskManager = new TaskManager(this);
        this.deathChestManager = new DeathChestManager(this);
        this.deathmatchManager = new DeathmatchManager(this);
        this.oreGenManager = new OreGenManager(this);
        this.soundManager = new SoundManager();
        this.recipeManager = new RecipeManager(this);
        this.worldManager = new WorldManager();

        this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public void loadNewGame() {
        setGameState(GameState.LOBBY);

        registerCommands();
        registerListeners();

        setForceStarted(false);
        setPvP(false);

        connectToDatabase();

        if (Bukkit.getWorld(getLobbyManager().getWorld(LobbyType.WAITING)) == null) {
            getWorldManager().createWorld("lobby", WorldType.FLAT, false);
        }
        if (Bukkit.getWorld(getLobbyManager().getWorld(LobbyType.ENDING)) == null) {
            getWorldManager().createWorld("lobby", WorldType.FLAT, false);
        }

        getBorderManager().setBorder();
        getOreGenManager().generateOres();

        getRecipeManager().registerRecipes();
        getCustomDropManager().loadCustomDrops();
        getTeamManager().loadTeams();
        getKitsManager().loadKits();
        // TODO getPerksManager().loadPerks();
        getGuiManager().loadInventories();

        getTaskManager().runGameChecking();
        getTaskManager().runScoreboardUpdate();
        getTaskManager().runActivityRewards();
        getTaskManager().runAutoBroadcast();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        if (this.gameState == gameState) return;
        this.gameState = gameState;

        switch (gameState) {

            case LOBBY:
                break;

            case STARTING:
                getTaskManager().startStartingCD();
                Utils.broadcast(Messages.GAME_STARTING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(getCurrentCountdown())));
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playStartingSound(player));
                break;

            case MINING:
                getPlayerManager().getOnlineList().forEach(getPlayerManager()::readyPlayer);
                getTeamManager().getTeams().forEach(uhcTeam -> uhcTeam.teleport(TeleportUtils.getSafeLocation()));

                getTaskManager().startMiningCD();
                Utils.broadcast(Messages.MINING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(getCurrentCountdown())));
                Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameStarted(player));
                break;

            case PVP:
                if (GameValues.TELEPORT_AFTER_MINING) {
                    getTeamManager().teleportAfterMining();
                }
                setPvP(true);
                getTaskManager().startPvPCD();

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

                break;
        }
    }

    public int getCurrentCountdown() {
        switch (gameState) {
            case LOBBY:
                return -1;
            case STARTING:
                return StartingCD.getCountdown();
            case MINING:
                return MiningCD.getCountdown();
            case PVP:
                return PvPCD.getCountdown();
            case DEATHMATCH:
                return DeathmatchCD.getCountdown();
            case ENDING:
                return EndingCD.getCountdown();
        }
        return 0;
    }

    public void onDisable() {
        getDeathChestManager().onDisable();
        getPlayerManager().onDisable();
        getTeamManager().onDisable();
        getTaskManager().onDisable();
        getGameManager().clearDrops();
        disconnectDatabase();
    }

    public boolean isPlaying() {
        return !gameState.equals(GameState.LOBBY) && !gameState.equals(GameState.STARTING);
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

    public void setUHCWinner() {

        if (getPlayerManager().getAliveList().isEmpty()) return;

        UHCPlayer winner = getPlayerManager().getAliveList().get(0);
        if (winner == null) return;

        for (UHCPlayer uhcPlayer : getPlayerManager().getAliveList()) {
            if (!uhcPlayer.isOnline()) return;
            if (uhcPlayer.getKills() > winner.getKills()) {
                winner = uhcPlayer;
            }
        }
        if (GameValues.TEAM_MODE) {
            for (UHCPlayer teamMember : winner.getTeam().getMembers()) {
                teamMember.setWinner(true);
            }
            return;
        }
        winner.setWinner(true);

    }

    public String getUHCWinner() {
        if (GameValues.TEAM_MODE) {
            UHCTeam winnerTeam = teamManager.getWinnerTeam();
            return winnerTeam != null ? (winnerTeam.getMembers().size() == 1 ? winnerTeam.getMembers().get(0).getName() : winnerTeam.getName()) : "None";
        }
        return getPlayerManager().getWinnerPlayer() != null ? getPlayerManager().getWinnerPlayer().getName() : "None";
    }

    public void clearDrops() {
        List<Entity> entList = GameValues.GAME_WORLD.getEntities();

        for (Entity current : entList) {
            if (current instanceof Item) {
                current.remove();
            }
        }
    }

    public void timber(Block block) {

        if (!(block.getType() == XMaterial.OAK_LOG.parseMaterial()
                || block.getType() == XMaterial.BIRCH_LOG.parseMaterial()
                || block.getType() == XMaterial.ACACIA_LOG.parseMaterial()
                || block.getType() == XMaterial.JUNGLE_LOG.parseMaterial()
                || block.getType() == XMaterial.SPRUCE_LOG.parseMaterial()
                || block.getType() == XMaterial.DARK_OAK_LOG.parseMaterial())) return;

        XSound.play(block.getLocation(), XSound.BLOCK_WOOD_BREAK.toString());
        block.breakNaturally(new ItemStack(XMaterial.OAK_PLANKS.parseMaterial(), 4));

        timber(block.getLocation().add(0, 1, 0).getBlock());
        timber(block.getLocation().add(1, 0, 0).getBlock());
        timber(block.getLocation().add(0, 1, 1).getBlock());

        timber(block.getLocation().subtract(0, 1, 0).getBlock());
        timber(block.getLocation().subtract(1, 0, 0).getBlock());
        timber(block.getLocation().subtract(0, 0, 1).getBlock());
    }

    public boolean isGameFull() {
        return playerManager.getOnlineList().size() >= playerManager.getMaxPlayers();
    }

    public MySQL getSQL() {
        return this.mysql;
    }

    public SQLGetter getData() {
        return this.data;
    }

    private void connectToDatabase() {
        String path = "settings.mysql";
        if (!config.getBoolean(path + ".enabled", false)) return;

        this.mysql = new MySQL(config.getString(path + ".host", "localhost"), config.getString(path + ".port", "3306"), config.getString(path + ".database", "uhcrun"), config.getString(path + ".username", "root"), config.getString(path + ".password", ""));
        this.data = new SQLGetter(this);
    }

    private void disconnectDatabase() {
        if (config.getBoolean("settings.MySQL.enabled", false)) {
            mysql.disconnect();
        }
    }

    public boolean isDatabaseConnected() {
        return this.mysql != null && this.mysql.hasConnection();
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
        PluginCommand command = plugin.getCommand(commandN);

        if (command == null) {
            plugin.getLogger().info("Error in registering command! (" + command + ")");
            return;
        }
        command.setExecutor(executor);
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

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public BorderManager getBorderManager() {
        return borderManager;
    }

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

    public boolean isVaultEnabled() {
        return config.getBoolean("settings.addons.use-Vault", false) && UHCRun.getVault() != null;
    }

    public boolean areLuckPermsEnabled() {
        return config.getBoolean("settings.addons.use-LuckPerms", false) && UHCRun.getLuckPerms() != null;
    }

    public boolean isProtocolLibEnabled() {
        return config.getBoolean("settings.addons.use-ProtocolLib", false) && Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
    }
}