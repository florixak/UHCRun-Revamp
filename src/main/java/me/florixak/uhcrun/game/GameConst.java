package me.florixak.uhcrun.game;

import me.florixak.uhcrun.config.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class GameConst {

    private static final GameManager gameManager = GameManager.getGameManager();
    private static final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

    public static final int MIN_PLAYERS = config.getInt("min-players", 2);
    public static final int MAX_PLAYERS = !gameManager.isTeamMode() ? Bukkit.getMaxPlayers() : gameManager.getTeamManager().getTeams().size()*config.getInt("settings.teams.max-size", 3);

    public static final int STARTING_COUNTDOWN = config.getInt("settings.game.countdowns.starting", 20);
    public static final int MINING_COUNTDOWN = config.getInt("settings.game.countdowns.mining", 600);
    public static final int PVP_COUNTDOWN = config.getInt("settings.game.countdowns.pvp", 600);
    public static final int DEATHMATCH_COUNTDOWN = config.getInt("settings.game.countdowns.deathmatch", 300);
    public static final int ENDING_COUNTDOWN = config.getInt("settings.game.countdowns.ending", 20);

    public static final double MONEY_FOR_WIN = config.getDouble("settings.statistics.rewards.win.money", 0);
    public static final double UHC_EXP_FOR_WIN = config.getDouble("settings.statistics.rewards.win.uhc-exp", 0);

    public static final double MONEY_FOR_LOSE = config.getDouble("settings.statistics.rewards.lose.money", 0);
    public static final double UHC_EXP_FOR_LOSE = config.getDouble("settings.statistics.rewards.lose.uhc-exp", 0);

    public static final double MONEY_FOR_KILL = config.getDouble("settings.statistics.rewards.kill.money", 0);
    public static final double UHC_EXP_FOR_KILL = config.getDouble("settings.statistics.rewards.kill.uhc-exp", 0);
    public static final int EXP_FOR_KILL = config.getInt("settings.statistics.rewards.kill.exp", 0);

    public static final double MONEY_FOR_ASSIST = config.getDouble("settings.statistics.rewards.assist.money", 0);
    public static final double UHC_EXP_FOR_ASSIST = config.getDouble("settings.statistics.rewards.assist.uhc-exp", 0);
    public static final int EXP_FOR_ASSIST = config.getInt("settings.statistics.rewards.assist.exp", 0);

}