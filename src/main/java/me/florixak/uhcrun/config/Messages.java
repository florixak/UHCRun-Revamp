package me.florixak.uhcrun.config;

import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public enum Messages {

    PREFIX("prefix"),

    INVALID_CMD("invalid-command"),
    NO_PERM("no-perm"),
    OFFLINE_PLAYER("offline-player"),
    ONLY_PLAYER("only-for-player"),
    NO_MONEY("no-money"),
    MONEY("money"),
    CANT_USE_NOW("cant-use-now"),

    CANT_PLACE("cant_place"),
    CANT_BREAK("cant_break"),

    JOIN("player.join"),
    QUIT("player.quit"),

    KILL("player.kill"),
    DEATH("player.death"),

    BORDER_SHRINK("game.border-shrink"),
    PVP("game.pvp"),
    PVP_IN("game.pvp-in"),
    MINING("game.mining"),
    DEATHMATCH_STARTED("game.deathmatch-started"),
    DEATHMATCH_STARTING("game.deathmatch-starting"),
    WINNER("game.winner"),

    PLAYERS_TO_START("game.players-to-start"),

    GAME_STARTING("game.starting"),
    GAME_ALREADY_STARTING("game.already-starting"),
    GAME_STARTING_CANCELED("game.starting-canceled"),
    GAME_STARTED("game.started"),
    GAME_ENDED("game.ended"),

    WIN_REWARDS("rewards.win"),
    LOSE_REWARDS("rewards.lose"),

    KITS_SELECTED("kits.selected"),
    PERKS_SELECTED("perks.selected"),

    NICK_NICKED("nick.nicked"),
    NICK_UNNICKED("nick.unnicked"),
    NICK_NOT_NICKED("nick.not-nicked"),
    NICK_MIN_CHARS("nick.min-chars"),
    NICK_OCCUPIED("nick.occupied"),

    REWARDS_PER_TIME("rewards.per-time"),

    LEVEL_UP("player.uhc-level.level-up"),

    SHOT_HP("player.shot-hp"),
    TOP_KILLERS("top-killers"),

    TEAM_JOIN("teams.join"),
    TEAM_LEAVE("teams.leave"),
    TEAM_FULL("teams.full"),
    TEAM_NOT_EXISTS("teams.not-exists"),
    TEAM_ALREADY_IN_TEAM("teams.already-in-team"),
    TEAM_NOT_IN_TEAM("teams.not-in-team"),
    TEAM_DEFEATED("teams.defeated"),

    RESTARTING("game.restarting"),

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

    public List<String> toList() {
        List<String> messages = new ArrayList<>();

        String prefix = config.getString("Messages." + PREFIX.getPath());
        for (String message : config.getStringList("Messages." + this.path)) {
            if (message != null && !message.isEmpty()) {
                messages.add(TextUtils.color(message
                        .replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : "")));
            }
        }
        return messages;
    }

    public String getPath() {
        return this.path;
    }
}