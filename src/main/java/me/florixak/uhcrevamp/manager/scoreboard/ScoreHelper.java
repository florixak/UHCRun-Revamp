package me.florixak.uhcrevamp.manager.scoreboard;

import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class ScoreHelper {

	private final Scoreboard scoreboard;
	private final Objective objective;
	private final Player player;

	public ScoreHelper(final Player player) {
		this.player = player;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("sidebar", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// Create Teams
		for (int i = 1; i <= 15; i++) {
			final Team team = scoreboard.registerNewTeam("SLOT" + i);
			team.addEntry(genEntry(i));
		}
		player.setScoreboard(scoreboard);
	}

	public void setTitle(String title) {
		title = setPlaceholders(title);
		objective.setDisplayName(title.length() > 32 ? title.substring(0, 32) : title);
	}

	public void setSlot(final int slot, String text) {
		final Team team = scoreboard.getTeam("SLOT" + slot);
		final String entry = genEntry(slot);
		if (!scoreboard.getEntries().contains(entry)) {
			objective.getScore(entry).setScore(slot);
		}

		text = setPlaceholders(text);
		final String pre = getFirstSplit(text);
		final String suf = getFirstSplit(ChatColor.getLastColors(pre) + getSecondSplit(text));
		team.setPrefix(pre);
		team.setSuffix(suf);
	}

	public void removeSlot(final int slot) {
		final String entry = genEntry(slot);
		if (scoreboard.getEntries().contains(entry)) {
			scoreboard.resetScores(entry);
		}
	}

	public String setPlaceholders(final String text) {
		return TextUtils.color(PlaceholderUtil.setPlaceholders(text, this.player));
	}

	public void setSlotsFromList(final List<String> list) {
		while (list.size() > 15) {
			list.remove(list.size() - 1);
		}

		int slot = list.size();

		if (slot < 15) {
			for (int i = (slot + 1); i <= 15; i++) {
				removeSlot(i);
			}
		}

		for (final String line : list) {
			setSlot(slot, line);
			slot--;
		}
	}

	private String genEntry(final int slot) {
		return ChatColor.values()[slot].toString();
	}

	private String getFirstSplit(final String s) {
		return s.length() > 16 ? s.substring(0, 16) : s;
	}

	private String getSecondSplit(String s) {
		if (s.length() > 32) {
			s = s.substring(0, 32);
		}
		return s.length() > 16 ? s.substring(16) : "";
	}

}
