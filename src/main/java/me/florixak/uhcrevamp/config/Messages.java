package me.florixak.uhcrevamp.config;

import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public enum Messages {

	PREFIX("prefix"),

	UHC_ADMIN_HELP("help.admin"),
	UHC_VIP_HELP("help.vip"),
	UHC_PLAYER_HELP("help.player"),

	INVALID_CMD("invalid-command"),
	NO_PERM("no-perm"),
	OFFLINE_PLAYER("offline-player"),
	ONLY_PLAYER("only-for-player"),
	NO_MONEY("no-money"),
	MONEY("money"),
	CURRENCY("currency"),
	CANCELLED_PURCHASE("purchase-cancel"),
	CANT_USE_NOW("cant-use-now"),

	CANT_PLACE("cant-place"),
	CANT_BREAK("cant-break"),

	JOIN("player.join"),
	QUIT("player.quit"),

	KILL("player.kill"),
	KILL_REWARDS("player.kill"),
	KILLSTREAK_NEW("player.new-killstreak"),
	DEATH("player.death"),

	TITLE_WIN("title.victory.title"),
	SUBTITLE_WIN("title.victory.subtitle"),
	TITLE_LOSE("title.lose.title"),
	SUBTITLE_LOSE("title.lose.subtitle"),

	PVP("game.pvp"),
	PVP_IN("game.pvp-in"),
	MINING("game.mining"),
	DEATHMATCH("game.deathmatch"),
	DEATHMATCH_IN("game.deathmatch-in"),

	BORDER_SHRINK("game.border-shrink"),

	PLAYERS_TO_START("game.players-to-start"),
	NOT_ENOUGH_PLAYERS("game.not-enough-players"),
	GAME_FORCE_STARTED("game.force-started"),
	GAME_PHASE_FORCE_SKIPPED("game.phase-force-skipped"),

	GAME_STARTING("game.starting"),
	GAME_ALREADY_STARTING("game.already-starting"),
	GAME_STARTING_CANCELED("game.starting-canceled"),
	GAME_STARTED("game.started"),
	GAME_ENDED("game.ended"),
	GAME_SOLO("game.solo"),
	GAME_TEAMS("game.teams"),
	GAME_RESTARTING("game.restarting"),
	GAME_FULL("game.full"),

	KICK_DUE_RESERVED_SLOT("game.kick-due-reserved-slot"),

	REWARDS_WIN("rewards.win"),
	REWARDS_LOSE("rewards.lose"),
	REWARDS_ACTIVITY("rewards.activity"),
	REWARDS_KILL("rewards.kill"),
	REWARDS_ASSIST("rewards.assist"),
	REWARDS_LEVEL_UP("rewards.level-up"),

	KITS_SELECTED("kits.selected"),
	KITS_INV_SELECTED("kits.inventory-selected"),
	KITS_INV_CLICK_TO_SELECT("kits.inventory-click-to-select"),
	KITS_DISABLED("kits.disabled"),
	KITS_SELECTED_NONE("kits.selected-none"),
	KITS_SB_DISABLED("kits.scoreboard-disabled"),
	KITS_MONEY_DEDUCT_INFO("kits.money-deduct-info"),
	KITS_MONEY_DEDUCT("kits.money-deduct"),
	KITS_COST("kits.cost"),

	PERKS_SELECTED("perks.selected"),
	PERKS_INV_SELECTED("perks.inventory-selected"),
	PERKS_INV_CLICK_TO_SELECT("perks.inventory-click-to-select"),
	PERKS_DISABLED("perks.disabled"),
	PERKS_SELECTED_NONE("perks.selected-none"),
	PERKS_SB_DISABLED("perks.scoreboard-disabled"),
	PERKS_MONEY_DEDUCT_INFO("perks.money-deduct-info"),
	PERKS_MONEY_DEDUCT("perks.money-deduct"),
	PERKS_COST("perks.cost"),

	LEVEL_UP("player.uhc-level.level-up"),

	SHOT_HP("player.shot-hp"),
	GAME_RESULTS("game-results"),

	TEAM_JOIN("teams.join"),
	TEAM_LEAVE("teams.leave"),
	TEAM_FULL("teams.full"),
	TEAM_ALREADY_IN("teams.already-in-team"),
	TEAM_NOT_IN("teams.not-in-team"),
	TEAM_DEFEATED("teams.defeated"),
	TEAM_NONE("teams.none"),
	TEAM_SOLO("teams.solo"),
	TEAM_NO_FRIENDLY_FIRE("teams.no-friendly-fire"),
	TEAM_ALL_FULL("teams.all-teams-full"),

	SETUP_SET_WAIT_LOBBY("setup.waiting-lobby-set"),
	SETUP_DEL_WAIT_LOBBY("setup.waiting-lobby-removed"),
	SETUP_SET_END_LOBBY("setup.ending-lobby-set"),
	SETUP_DEL_END_LOBBY("setup.ending-lobby-removed"),
	SETUP_SET_DEATHMATCH("setup.deathmatch-set"),
	SETUP_RESET_DEATHMATCH("setup.deathmatch-reset");

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
			return TextUtils.color("&cMessage not found! &7(" + this.path + ")");
		}

		String prefix = config.getString("Messages." + PREFIX.getPath());
		String currency = config.getString("Messages." + CURRENCY.getPath());
		return TextUtils.color(message
				.replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : "")
				.replace("%currency%", currency != null && !currency.isEmpty() ? currency : "")
		);
	}


    /*public String toString(String... replace) {
        String message = config.getString("Messages." + this.path);

        if (message == null || message.isEmpty()) {
            return TextUtils.color("&cMessage not found! &7(" + this.path + ")");
        }

        String prefix = config.getString("Messages." + PREFIX.getPath());

        for (int i = 0; i < replace.length; i++) {
            message = message.replace(replace[i], replace[i+1]);
        }

        return TextUtils.color(message.replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : ""));
    }*/

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