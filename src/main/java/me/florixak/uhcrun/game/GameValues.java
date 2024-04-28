package me.florixak.uhcrun.game;

import me.florixak.uhcrun.config.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class GameValues {

    private static final GameManager gameManager = GameManager.getGameManager();
    private static final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

    public static final World GAME_WORLD = Bukkit.getWorld("world");

    public static final boolean TEAM_MODE = getConfigBoolean("settings.teams.team-mode", true);
    public static final boolean KITS_ENABLED = getConfigBoolean("settings.kits.enabled", true);;
    public static final boolean PERKS_ENABLED = getConfigBoolean("settings.perks.enabled", true);;
    public static final boolean FRIENDLY_FIRE = getConfigBoolean("settings.teams.friendly-fire", false);
    public static final boolean TELEPORT_AFTER_MINING = getConfigBoolean("settings.game.teleport-after-mining", true);;
    public static final boolean CUSTOM_DROPS_ENABLED = getConfigBoolean("settings.game.custom-drops", true);;
    public static final boolean STATS_ADD_ON_END = getConfigBoolean("settings.statistics.add-up-game-ends", false);;
    public static final boolean DEATH_CHESTS_ENABLED = getConfigBoolean("settings.death-chest.enabled", true);;
    public static final boolean EXPLOSIONS_DISABLED = getConfigBoolean("settings.game.no-explosions", true);;
    public static final boolean RANDOM_DROPS_ENABLED = getConfigBoolean("settings.game.random-drops", false);;
    public static final boolean NETHER_ENABLED = getConfigBoolean("settings.game.allow-nether", false);;

    public static final int TEAM_SIZE = getConfigInt("settings.teams.max-size", 3);
    public static final int MIN_PLAYERS = getConfigInt("min-players", 2);
    public static final int MAX_PLAYERS = !TEAM_MODE ? Bukkit.getMaxPlayers() : gameManager.getTeamManager().getTeams().size()*TEAM_SIZE;

    public static final int STARTING_COUNTDOWN = getConfigInt("settings.game.countdowns.starting", 20);
    public static final int MINING_COUNTDOWN = getConfigInt("settings.game.countdowns.mining", 600);
    public static final int PVP_COUNTDOWN = getConfigInt("settings.game.countdowns.pvp", 600);
    public static final int DEATHMATCH_COUNTDOWN = getConfigInt("settings.game.countdowns.deathmatch", 300);
    public static final int ENDING_COUNTDOWN = getConfigInt("settings.game.countdowns.ending", 20);

    public static final double MONEY_FOR_WIN = getConfigInt("settings.statistics.rewards.win.money", 0);
    public static final double UHC_EXP_FOR_WIN = getConfigInt("settings.statistics.rewards.win.uhc-exp", 0);

    public static final double MONEY_FOR_LOSE = getConfigInt("settings.statistics.rewards.lose.money", 0);
    public static final double UHC_EXP_FOR_LOSE = getConfigInt("settings.statistics.rewards.lose.uhc-exp", 0);

    public static final double MONEY_FOR_KILL = getConfigInt("settings.statistics.rewards.kill.money", 0);
    public static final double UHC_EXP_FOR_KILL = getConfigInt("settings.statistics.rewards.kill.uhc-exp", 0);
    public static final int EXP_FOR_KILL = getConfigInt("settings.statistics.rewards.kill.exp", 0);

    public static final double MONEY_FOR_ASSIST = getConfigInt("settings.statistics.rewards.assist.money", 0);
    public static final double UHC_EXP_FOR_ASSIST = getConfigInt("settings.statistics.rewards.assist.uhc-exp", 0);
    public static final int EXP_FOR_ASSIST = getConfigInt("settings.statistics.rewards.assist.exp", 0);

    private static boolean getConfigBoolean(String path, boolean def) {
      return config.getBoolean(path, def);
    }
    private static int getConfigInt(String path, int def) {
        return config.getInt(path, def);
    }
}

