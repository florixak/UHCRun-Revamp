package me.florixak.uhcrevamp.manager.scoreboard;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

	private final Map<UUID, ScoreHelper> players;

	private final String title;
	private final String footer;
	private final List<String> waiting;
	private final List<String> starting;
	private final List<String> mining;
	private final List<String> pvp;
	private final List<String> deathmatch;
	private final List<String> ending;

	private final GameManager gameManager;

	public ScoreboardManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.players = new HashMap<>();

		final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SCOREBOARD).getConfig();

		this.title = config.getString("scoreboard.title");
		this.footer = config.getString("scoreboard.footer");

		this.waiting = config.getStringList("scoreboard.waiting");
		this.starting = config.getStringList("scoreboard.starting");
		this.mining = config.getStringList("scoreboard.mining");
		this.pvp = config.getStringList("scoreboard.pvp");
		this.deathmatch = config.getStringList("scoreboard.deathmatch");
		this.ending = config.getStringList("scoreboard.ending");
	}

	public void setScoreboard(final Player p) {
		if (!players.containsKey(p.getUniqueId())) {
			players.put(p.getUniqueId(), updateScoreboard(p.getUniqueId()));
		}
	}

	public ScoreHelper updateScoreboard(final UUID uuid) {

		final Player p = Bukkit.getPlayer(uuid);
		if (p == null) return null;

		ScoreHelper helper = players.get(p.getUniqueId());
		if (helper == null) helper = new ScoreHelper(p);
		helper.setTitle(title);
		switch (gameManager.getGameState()) {
			case LOBBY:
				helper.setSlotsFromList(this.waiting);
				break;
			case STARTING:
				helper.setSlotsFromList(this.starting);
				break;
			case MINING:
				helper.setSlotsFromList(this.mining);
				break;
			case PVP:
				helper.setSlotsFromList(this.pvp);
				break;
			case DEATHMATCH:
				helper.setSlotsFromList(this.deathmatch);
				break;
			case ENDING:
				helper.setSlotsFromList(this.ending);
				break;
		}
		return helper;
	}

	public String getFooter() {
		return footer;
	}

	public void removeScoreboard(final Player p) {
		if (players.containsKey(p.getUniqueId())) {
			players.remove(p.getUniqueId());
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
}