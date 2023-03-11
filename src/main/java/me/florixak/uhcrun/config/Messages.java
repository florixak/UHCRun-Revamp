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

    GAME_STARTING("game-starting"),
    GAME_ALREADY_STARTING("game-already-starting"),
    GAME_STARTING_CANCELED("game-starting-canceled"),
    GAME_STARTED("game-started"),
    GAME_ENDED("game-ended"),

    KITS_SELECTED("kits.selected"),
    PERKS_SELECTED("perks.selected"),

    CLICK_BUY_INV("inv.click-to-buy"),
    CLICK_SELECT_INV("inv.click-to-select"),
    SELECTED_INV("inv.selected"),
    DISABLED("inv.disabled"),

    REWARDS_PER_TIME("rewards-per-time"),
    GG_REWARD("gg-rewards"),

    LEVEL_UP("level-up"),

    VOTED("voted"),

    SHOT_HP("shot-hp"),

    RESTARTING("restarting"),

    CREATOR_ON("creator-on"),
    CREATOR_OFF("creator-off"),
    CREATOR_BEFORE_START("creator-before-start"),

    SETLOBBY_WAITING("setlobby.waiting"),
    SETLOBBY_ENDING("setlobby.ending"),

    CANT_PLACE("cant_place"),
    CANT_BREAK("cant_break");


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
            return "Survival: message not found (" + this.path + ")";
        }

        String prefix = config.getString("Messages." + PREFIX.getPath());
        return TextUtils.color(message.replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : ""));
    }

    public String getPath() {
        return this.path;
    }
}