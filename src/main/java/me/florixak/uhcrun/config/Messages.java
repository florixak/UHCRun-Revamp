package me.florixak.uhcrun.config;

import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

public enum Messages {

    PREFIX("prefix"),

    INVALID_CMD("invalid-command"),
    NO_PERM("no-perm"),
    OFFLINE_PLAYER("offline-player"),
    NOT_PLAYER("not-player"),
    NO_MONEY("no-money"),
    MONEY("money"),
    CANT_USE_NOW("cant-use-now"),

    CANT_PLACE("cant_place"),
    CANT_BREAK("cant_break"),

    JOIN("join"),
    QUIT("quit"),

    KILL("kill"),
    DEATH("death"),

    BORDER_SHRINK("border-shrink"),
    PVP("pvp"),
    MINING("mining"),
    DEATHMATCH_STARTED("deathmatch-started"),
    DEATHMATCH_STARTING("deathmatch-starting"),
    WINNER("winner"),

    PLAYERS_TO_START("players-to-start"),

    GAME_STARTING("game-phase.starting"),
    GAME_ALREADY_STARTING("game-phase.already-starting"),
    GAME_STARTING_CANCELED("game-phase.starting-canceled"),
    GAME_STARTED("game-phase.started"),
    GAME_ENDED("game-phase.ended"),

    KITS_SELECTED("kits.selected"),
    PERKS_SELECTED("perks.selected"),

    CLICK_BUY_INV("inv.click-to-buy"),
    CLICK_SELECT_INV("inv.click-to-select"),
    SELECTED_INV("inv.selected"),
    DISABLED("inv.disabled"),

    NICK_NICKED("nick.nicked"),
    NICK_UNNICKED("nick.unnicked"),
    NICK_NOT_NICKED("nick.not-nicked"),
    NICK_MIN_CHARS("nick.min-chars"),
    NICK_OCCUPIED("nick.occupied"),

    REWARDS_PER_TIME("rewards-per-time"),
    GG_REWARD("gg-rewards"),

    LEVEL_UP("level-up"),

    SHOT_HP("shot-hp"),

    TEAM_JOIN("teams.join"),
    TEAM_LEAVE("teams.leave"),
    TEAM_FULL("teams.full"),
    TEAM_NOT_EXISTS("teams.not-exists"),
    TEAM_ALREADY_IN_TEAM("teams.already-in-team"),
    TEAM_NOT_IN_TEAM("teams.not-in-team"),
    TEAM_LIST("teams.list"),
    TEAM_NO_TEAMS("teams.no-teams"),

    RESTARTING("restarting"),

    SETLOBBY_WAITING("setlobby.waiting"),
    SETLOBBY_ENDING("setlobby.ending");


    private static FileConfiguration config;
    private String path;

    Messages(String path) {
        this.path = path;
    }

    public static void setConfiguration(FileConfiguration c) {
        config = c;
    }

    @Override
    public String toString() {
        String message = config.getString("Messages." + this.path);

        if (message == null || message.isEmpty()) {
            return "UHCRun: message not found (" + this.path + ")";
        }

        String prefix = config.getString("Messages." + PREFIX.getPath());
        return TextUtils.color(message.replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : ""));
    }

    public String getPath() {
        return this.path;
    }
}