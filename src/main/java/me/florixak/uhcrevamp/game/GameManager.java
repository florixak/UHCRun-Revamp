package me.florixak.uhcrevamp.game;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.commands.*;
import me.florixak.uhcrevamp.config.ConfigManager;
import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.assists.DamageTrackerManager;
import me.florixak.uhcrevamp.game.customDrop.CustomDropManager;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipeManager;
import me.florixak.uhcrevamp.game.deathChest.DeathChestManager;
import me.florixak.uhcrevamp.game.kits.KitsManager;
import me.florixak.uhcrevamp.game.perks.PerksManager;
import me.florixak.uhcrevamp.game.player.PlayerListener;
import me.florixak.uhcrevamp.game.player.PlayerManager;
import me.florixak.uhcrevamp.game.statistics.StatisticsManager;
import me.florixak.uhcrevamp.game.teams.TeamManager;
import me.florixak.uhcrevamp.game.worldGenerator.OreGenManager;
import me.florixak.uhcrevamp.game.worldGenerator.OreGeneratorListener;
import me.florixak.uhcrevamp.game.worldGenerator.WorldGeneratorListener;
import me.florixak.uhcrevamp.game.worldGenerator.WorldManager;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.listener.*;
import me.florixak.uhcrevamp.listener.events.GameEndEvent;
import me.florixak.uhcrevamp.manager.*;
import me.florixak.uhcrevamp.manager.scoreboard.ScoreboardManager;
import me.florixak.uhcrevamp.manager.scoreboard.TabManager;
import me.florixak.uhcrevamp.sql.MySQL;
import me.florixak.uhcrevamp.sql.SQLGetter;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XSound;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

	private final UHCRevamp plugin;
	private final FileConfiguration config;

	private GameState gameState;

	private MySQL mysql;
	private SQLGetter data;
	private static GameManager gameManager;
	private final ConfigManager configManager;
	private final PlayerManager playerManager;
	private final TeamManager teamManager;
	private final StatisticsManager statisticsManager;
	private final DeathmatchManager deathmatchManager;
	private final PerksManager perksManager;
	private final KitsManager kitsManager;
	private final TaskManager taskManager;
	private final BorderManager borderManager;
	private final ScoreboardManager scoreboardManager;
	private final TabManager tabManager;
	private final LobbyManager lobbyManager;
	private final CustomDropManager customDropManager;
	private final SoundManager soundManager;
	private final CustomRecipeManager customRecipeManager;
	private final DeathChestManager deathChestManager;
	private final OreGenManager oreGenManager;
	private final WorldManager worldManager;
	private final MenuManager menuManager;
	private final DamageTrackerManager damageTrackerManager;

	private boolean resistance;

	public GameManager(final UHCRevamp plugin) {
		this.plugin = plugin;
		gameManager = this;

		this.configManager = new ConfigManager();
		this.configManager.loadFiles(plugin);
		this.worldManager = new WorldManager();
		this.borderManager = new BorderManager();
		this.teamManager = new TeamManager(this);
		this.playerManager = new PlayerManager(this);
		this.scoreboardManager = new ScoreboardManager(this);
		this.tabManager = new TabManager();
		this.lobbyManager = new LobbyManager(this);
		this.kitsManager = new KitsManager(this);
		this.perksManager = new PerksManager(this);
		this.customDropManager = new CustomDropManager(this);
		this.taskManager = new TaskManager(this);
		this.deathChestManager = new DeathChestManager(this);
		this.deathmatchManager = new DeathmatchManager(this);
		this.oreGenManager = new OreGenManager(this);
		this.soundManager = new SoundManager();
		this.customRecipeManager = new CustomRecipeManager(this);
		this.menuManager = new MenuManager();
		this.statisticsManager = new StatisticsManager(this);
		this.damageTrackerManager = new DamageTrackerManager();

		this.config = getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
	}

	public void loadNewGame() {
		connectToDatabase();
		setGameState(GameState.LOBBY);

		getLobbyManager().checkLobbies();

		registerCommands();
		registerListeners();

		getOreGenManager().loadOres();
		getWorldManager().createNewUHCWorld();

		getBorderManager().setBorder();

		getRecipeManager().registerRecipes();
		getCustomDropManager().loadDrops();
		getTeamManager().loadTeams();
		getPlayerManager().setMaxPlayers();
		getKitsManager().loadKits();
		getPerksManager().loadPerks();
		getStatisticsManager().loadTopStatistics();

		getTaskManager().runGameCheckTask();
		getTaskManager().runScoreboardUpdateTask();
//        getTaskManager().runPlayingTimeTask();

		setResistance(false);
	}

	public GameState getGameState() {
		return this.gameState;
	}

	public void setGameState(final GameState gameState) {
		if (this.gameState == gameState) return;
		this.gameState = gameState;

		switch (gameState) {
			case LOBBY:
				break;

			case STARTING:
				getTaskManager().startStartingTask();
				Utils.broadcast(PlaceholderUtil.setPlaceholders(Messages.GAME_STARTING.toString(), null));
				Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameStartingSound(player));
				break;

			case MINING:
				getPlayerManager().getOnlinePlayers().forEach(uhcPlayer -> getPlayerManager().setPlayerForGame(uhcPlayer));

				if (GameValues.TEAM.TEAM_MODE) {
					getTeamManager().teleportInToGame();
				} else {
					getPlayerManager().teleportInToGame();
				}

				getTaskManager().startMiningTask();
				Utils.broadcast(PlaceholderUtil.setPlaceholders(Messages.MINING.toString(), null));
				Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameStartedSound(player));
				break;

			case PVP:
				if (GameValues.GAME.TELEPORT_AFTER_MINING) {
					if (GameValues.TEAM.TEAM_MODE) {
						getTeamManager().teleportAfterMining();
					} else {
						getPlayerManager().teleportAfterMining();
					}
				}
				getTaskManager().startResistanceTask();
				getTaskManager().startPvPTask();

				Utils.broadcast(Messages.BORDER_SHRINK.toString());
				break;

			case DEATHMATCH:
				getDeathmatchManager().prepareDeathmatch();
				getTaskManager().startDeathmatchTask();

				Utils.broadcast(Messages.DEATHMATCH.toString());
				Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playDeathmatchSound(player));
				break;

			case ENDING:
				Utils.broadcast(Messages.GAME_ENDED.toString());
				Bukkit.getOnlinePlayers().forEach(player -> getSoundManager().playGameEndSound(player));

				getPlayerManager().setUHCWinner();
				plugin.getServer().getPluginManager().callEvent(new GameEndEvent(getPlayerManager().getUHCWinner()));
				//getTaskManager().stopPlayingTimeTask();
				getTaskManager().startEndingTask();
				break;
		}
	}

	public synchronized int getCurrentCountdown() {
		switch (gameState) {
			case LOBBY:
				return -1;
			case STARTING:
				return getTaskManager().getStartingPhaseTask().getCountdown();
			case MINING:
				return getTaskManager().getMiningPhaseTask().getCountdown();
			case PVP:
				return getTaskManager().getPvPTask().getCountdown();
			case DEATHMATCH:
				return getTaskManager().getDeathmatchTask().getCountdown();
			case ENDING:
				return getTaskManager().getEndingTask().getCountdown();
			default:
				return 0;
		}
	}

	public void onDisable() {
		getDeathChestManager().onDisable();
		getDamageTrackerManager().onDisable();
		getCustomDropManager().onDisable();
		getRecipeManager().onDisable();
		getTeamManager().onDisable();
		getPlayerManager().onDisable();
		getTaskManager().onDisable();
		clearDrops();
		disconnectDatabase();
	}

	public boolean isStarting() {
		return gameState.equals(GameState.STARTING);
	}

	public boolean isPlaying() {
		return !gameState.equals(GameState.LOBBY) && !gameState.equals(GameState.STARTING);
	}

	public boolean isEnding() {
		return gameState.equals(GameState.ENDING);
	}

	public boolean isResistance() {
		return this.resistance;
	}

	public void setResistance(final boolean b) {
		this.resistance = b;
	}

	public void clearDrops() {
		if (Bukkit.getWorld(GameValues.WORLD_NAME) == null) return;
		final List<Entity> entList = Bukkit.getWorld(GameValues.WORLD_NAME).getEntities();

		for (final Entity current : entList) {
			if (current instanceof Item) {
				current.remove();
			}
		}
	}

	public void timber(final Block block, final BlockBreakEvent event) {

		if (!GameValues.WOOD_LOGS.contains(block.getType())) return;

		XSound.play(block.getLocation(), XSound.BLOCK_WOOD_BREAK.toString());
//		if (gameManager.getCustomDropManager().hasBlockCustomDrop(block.getType())) {
//			CustomDrop customDrop = gameManager.getCustomDropManager().getCustomBlockDrop(block.getType());
//			block.getDrops().clear();
//			customDrop.dropItem(event);
//		} else {
		block.getDrops().clear();
		block.breakNaturally(new ItemStack(block.getType()));
//		}

		timber(block.getLocation().add(0, 1, 0).getBlock(), event);
		timber(block.getLocation().add(1, 0, 0).getBlock(), event);
		timber(block.getLocation().add(0, 1, 1).getBlock(), event);

		timber(block.getLocation().subtract(0, 1, 0).getBlock(), event);
		timber(block.getLocation().subtract(1, 0, 0).getBlock(), event);
		timber(block.getLocation().subtract(0, 0, 1).getBlock(), event);
	}

	public boolean isGameFull() {
		return playerManager.getPlayers().size() >= playerManager.getMaxPlayers();
	}

	public MySQL getSQL() {
		return this.mysql;
	}

	public SQLGetter getDatabase() {
		return this.data;
	}

	private void connectToDatabase() {
		try {
			final String path = "settings.mysql";
			if (config == null || !config.getBoolean(path + ".enabled", false)) return;

			final String host = config.getString(path + ".host", "localhost");
			final String port = config.getString(path + ".port", "3306");
			final String database = config.getString(path + ".database", "uhcrevamp");
			final String username = config.getString(path + ".username", "root");
			final String password = config.getString(path + ".password", "");

			Bukkit.getLogger().info("Connecting to MySQL database...");

			this.mysql = new MySQL(host, port, database, username, password);
			if (this.mysql.hasConnection()) {
				this.data = new SQLGetter(this);
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("Failed to connect to MySQL database!");
		}

	}

	private void disconnectDatabase() {
		if (config.getBoolean("settings.mysql.enabled", false) && mysql != null && mysql.hasConnection()) {
			mysql.disconnect();
		}
	}

	public boolean isDatabaseConnected() {
		return this.mysql != null && this.mysql.hasConnection();
	}

	private void registerListeners() {
		final List<Listener> listeners = new ArrayList<>();

		listeners.add(new GameListener(this));
		listeners.add(new EntityListener(this));
		listeners.add(new PlayerListener(this));
		listeners.add(new ChatListener(this));
		listeners.add(new InteractListener(this));
		listeners.add(new InventoryClickListener(this));
		listeners.add(new OreGeneratorListener(this));
		listeners.add(new WorldGeneratorListener(this));
		if (!UHCRevamp.useOldMethods) listeners.add(new AnvilClickListener());

		for (final Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}

	private void registerCommands() {
		registerCommand("uhc", new UHCCommand(this));
		registerCommand("forcestart", new ForceStartCommand(this));
		registerCommand("forceskip", new ForceSkipCommand(this));
		registerCommand("team", new TeamCommand(this));
		registerCommand("workbench", new WorkbenchCommand(this));
		registerCommand("anvil", new AnvilCommand(this));
		registerCommand("kits", new KitsCommand(this));
		registerCommand("perks", new PerksCommand(this));
		registerCommand("recipes", new RecipesCommand(this));
		registerCommand("statistics", new StatisticsCommand(this));
		registerCommand("revive", new ReviveCommand(this));
	}

	private void registerCommand(final String commandN, final CommandExecutor executor) {
		final PluginCommand command = plugin.getCommand(commandN);

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

	public CustomRecipeManager getRecipeManager() {
		return customRecipeManager;
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

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public StatisticsManager getStatisticsManager() {
		return statisticsManager;
	}

	public DamageTrackerManager getDamageTrackerManager() {
		return damageTrackerManager;
	}
}