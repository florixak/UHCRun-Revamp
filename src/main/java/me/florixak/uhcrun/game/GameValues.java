package me.florixak.uhcrun.game;

import me.florixak.uhcrun.config.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class GameValues {

    private static final GameManager gameManager = GameManager.getGameManager();
    private static final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

    public static int ERROR_INT_VALUE = -1;
    public static int COLUMNS = 9;

    public static final World GAME_WORLD = Bukkit.getWorld("world");

    public static final String CHAT_FORMAT_IN_GAME = getConfigString("settings.chat.in-game-format", "");
    public static final String CHAT_FORMAT_DEAD = getConfigString("settings.chat.dead-format", "");
    public static final String CHAT_FORMAT_LOBBY = getConfigString("settings.chat.lobby-format", "");
    public static final List<String> CHAT_BLOCKED_COMMANDS = getConfigStringList("settings.chat.blocked-commands");

    public static final double INIT_BORDER_SIZE = getConfigDouble("settings.border.size", 300);
    public static final double BORDER_SPEED = getConfigDouble("settings.border.speed", 0);

    public static final boolean TEAM_MODE = getConfigBoolean("settings.teams.team-mode", true);
    public static final boolean KITS_ENABLED = getConfigBoolean("settings.kits.enabled", true);
    public static final boolean BOUGHT_KITS_FOREVER = getConfigBoolean("settings.kits.bought-forever", true);
    public static final boolean PERKS_ENABLED = getConfigBoolean("settings.perks.enabled", true);
    public static final boolean FRIENDLY_FIRE = getConfigBoolean("settings.teams.friendly-fire", false);
    public static final boolean TELEPORT_AFTER_MINING = getConfigBoolean("settings.game.teleport-after-mining", true);
    public static final boolean CUSTOM_DROPS_ENABLED = getConfigBoolean("settings.game.custom-drops", true);
    public static final boolean STATS_ADD_ON_END = getConfigBoolean("settings.statistics.add-up-game-ends", false);
    public static final boolean DEATH_CHESTS_ENABLED = getConfigBoolean("settings.death-chest.enabled", true);
    public static final boolean EXPLOSIONS_DISABLED = getConfigBoolean("settings.game.no-explosions", true);
    public static final boolean RANDOM_DROPS_ENABLED = getConfigBoolean("settings.game.random-drops", false);
    public static final boolean NETHER_ENABLED = getConfigBoolean("settings.game.allow-nether", false);
    public static final boolean PROJECTILE_HIT_HP_ENABLED = getConfigBoolean("settings.game.projectile-hit-hp", false);

    public static final boolean CAN_USE_VAULT = getConfigBoolean("settings.addons.use-Vault", true);
    public static final boolean CAN_USE_LUCKPERMS = getConfigBoolean("settings.addons.use-LuckPerms", true);
    public static final boolean CAN_USE_PLACEHOLDERAPI = getConfigBoolean("settings.addons.use-PlaceholderAPI", true);
    public static final boolean CAN_USE_PROTOCOLLIB = getConfigBoolean("settings.addons.use-ProtocolLib", true);

    public static final int TEAM_SIZE = getConfigInt("settings.teams.max-size", 3);
    public static final int MIN_PLAYERS = getConfigInt("settings.game.min-players", 2);

    public static final int STARTING_COUNTDOWN = getConfigInt("settings.game.countdowns.starting", 20);
    public static final int MINING_COUNTDOWN = getConfigInt("settings.game.countdowns.mining", 600);
    public static final int PVP_COUNTDOWN = getConfigInt("settings.game.countdowns.pvp", 600);
    public static final int DEATHMATCH_COUNTDOWN = getConfigInt("settings.game.countdowns.deathmatch", 300);
    public static final int ENDING_COUNTDOWN = getConfigInt("settings.game.countdowns.ending", 20);

    public static final double BASE_REWARD = getConfigDouble("settings.rewards.base-reward", 100);
    public static final double REWARD_COEFFICIENT = getConfigDouble("settings.rewards.reward-coefficient", 1);

    public static final double MONEY_FOR_WIN = getConfigDouble("settings.rewards.win.money", 0);
    public static final double UHC_EXP_FOR_WIN = getConfigInt("settings.rewards.win.uhc-exp", 0);

    public static final double MONEY_FOR_LOSE = getConfigDouble("settings.rewards.lose.money", 0);
    public static final double UHC_EXP_FOR_LOSE = getConfigInt("settings.rewards.lose.uhc-exp", 0);

    public static final double MONEY_FOR_KILL = getConfigDouble("settings.rewards.kill.money", 0);
    public static final double UHC_EXP_FOR_KILL = getConfigDouble("settings.rewards.kill.uhc-exp", 0);
    public static final double EXP_FOR_KILL = getConfigDouble("settings.rewards.kill.exp", 0);

    public static final double MONEY_FOR_ASSIST = getConfigDouble("settings.rewards.assist.money", 0);
    public static final double UHC_EXP_FOR_ASSIST = getConfigDouble("settings.rewards.assist.uhc-exp", 0);
    public static final double EXP_FOR_ASSIST = getConfigDouble("settings.rewards.assist.exp", 0);

    public static final boolean BROADCAST_ENABLED = getConfigBoolean("settings.auto-broadcast.enabled", true);
    public static final int BROADCAST_INTERVAL = getConfigInt("settings.auto-broadcast.period", 300);

    public static final boolean UHC_LEVEL_ENABLED = getConfigBoolean("settings.statistics.uhc-level.enabled", true);
    public static final int FIRST_UHC_LEVEL = getConfigInt("settings.statistics.uhc-level.first-uhc-level", 0);
    public static final double FIRST_REQUIRED_EXP = getConfigDouble("settings.statistics.uhc-level.first-required-exp", 100.0);
    public static final double EXP_MULTIPLIER = getConfigDouble("settings.statistics.uhc-level.exp-multiplier", 3.75);

    public static final boolean ACTIVITY_REWARDS_ENABLED = getConfigBoolean("settings.rewards.activity.enabled", true);
    public static final int ACTIVITY_REWARDS_INTERVAL = getConfigInt("settings.rewards.activity.period", 300);
    public static final double ACTIVITY_REWARDS_MONEY = getConfigDouble("settings.rewards.activity.money", 10);
    public static final double ACTIVITY_REWARDS_EXP = getConfigDouble("settings.rewards.activity.uhc-exp", 20);

    public static final String STATS_PLAYER_STATS_DIS_ITEM = getConfigString("settings.statistics.player-stats.display-item", "STONE");
    public static final String STATS_PLAYER_STATS_CUST_NAME = getConfigString("settings.statistics.player-stats.custom-name", "YOUR STATS");
    public static final List<String> STATS_PLAYER_STATS_LORE = getConfigStringList("settings.statistics.player-stats.lore");
    public static final int STATS_PLAYER_STATS_SLOT = 13;

    public static final String STATS_TOP_STATS_DIS_ITEM = getConfigString("settings.statistics.top-stats.display-item", "PAPER");
    public static final String STATS_TOP_STATS_CUST_NAME = getConfigString("settings.statistics.top-stats.custom-name", "TOP STATS");
    public static final List<String> STATS_TOP_STATS_LORE = getConfigStringList("settings.statistics.top-stats.lore");
    public static final List<String> STATS_DISPLAYED_TOPS = getConfigStringList("settings.statistics.displayed-tops");
    public static final int STATS_TOP_SLOT = 17;

    public static final String INV_TEAMS_TITLE = getConfigString("settings.selectors.teams.display-name", "Teams");
    public static final String INV_TEAMS_DIS_ITEM = getConfigString("settings.selectors.teams.display-item", "BARRIER");
    public static final String INV_KITS_TITLE = getConfigString("settings.selectors.kits.display-name", "Kits");
    public static final String INV_KITS_DIS_ITEM = getConfigString("settings.selectors.kits.display-item", "BARRIER");
    public static final String INV_PERKS_TITLE = getConfigString("settings.selectors.perks.display-name", "Perks");
    public static final String INV_PERKS_DIS_ITEM = getConfigString("settings.selectors.perks.display-item", "BARRIER");
    public static final String INV_STATS_TITLE = getConfigString("settings.selectors.statistics.display-name", "Statistics");
    public static final String INV_STATS_DIS_ITEM = getConfigString("settings.selectors.statistics.display-item", "BARRIER");


    private static boolean getConfigBoolean(String path, boolean def) {
      return config.getBoolean(path, def);
    }
    private static String getConfigString(String path, String def) {
      return config.getString(path, def);
    }
    private static int getConfigInt(String path, int def) {
        return config.getInt(path, def);
    }
    private static double getConfigDouble(String path, double def) {
        return config.getDouble(path, def);
    }
    private static List<String> getConfigStringList(String path) {
        return config.getStringList(path);
    }
}

