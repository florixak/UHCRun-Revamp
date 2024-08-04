package me.florixak.uhcrevamp.game.teams;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.TeleportUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamManager {

	private final FileConfiguration teamsConfig;
	private final List<UHCTeam> teamsList;

	public TeamManager(final GameManager gameManager) {

		this.teamsConfig = gameManager.getConfigManager().getFile(ConfigType.TEAMS).getConfig();
		this.teamsList = new ArrayList<>();
	}

	public void loadTeams() {
		if (!GameValues.TEAM.TEAM_MODE) return;

		if (teamsConfig.contains("teams") && teamsConfig.getConfigurationSection("teams").getKeys(false).isEmpty()) {
			UHCRevamp.getInstance().getLogger().info("Team file is empty!");
			return;
		}

		for (final String teamName : teamsConfig.getConfigurationSection("teams").getKeys(false)) {
			final ItemStack display_item = XMaterial.matchXMaterial(teamsConfig.getString("teams." + teamName + ".display-item", "BARRIER")
					.toUpperCase()).get().parseItem();
			final int durability = teamsConfig.getInt("teams." + teamName + ".durability");
			final String color = teamsConfig.getString("teams." + teamName + ".color");
			final UHCTeam team = new UHCTeam(display_item, durability, teamName, color, GameValues.TEAM.TEAM_SIZE);
			this.teamsList.add(team);
		}
	}

	public UHCTeam getTeam(final String name) {
		return teamsList.stream().filter(team -> team.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public List<UHCTeam> getTeamsList() {
		return this.teamsList;
	}

	public String getTeamsString() {
		return getTeamsList().stream().map(UHCTeam::getDisplayName).collect(Collectors.joining(", "));
	}

	public List<UHCTeam> getLivingTeams() {
		return teamsList.stream().filter(UHCTeam::isAlive).collect(Collectors.toList());
	}

	public void addTeam(final UHCTeam team) {
		if (exists(team.getName()) || team == null) return;
		this.teamsList.add(team);
	}

    /*public void removeTeam(String teamName) {
        if (!exists(teamName) || teamName == null) return;

        List<String> teams_list = teamsConfig.getStringList("teams");
        teams_list.remove(teamName);

        UHCTeam team = getTeam(teamName);

        this.teamsList.remove(team);
        teamsConfig.set("teams", teams_list);
        gameManager.getConfigManager().getFile(ConfigType.TEAMS).save();
    }*/

	public void joinRandomTeam(final UHCPlayer uhcPlayer) {
		if (uhcPlayer.hasTeam()) return;
		final UHCTeam team = findFreeTeam();
		if (team == null) {
			uhcPlayer.setSpectator();
			return;
		}
		team.addMember(uhcPlayer);
	}

	public UHCTeam getWinnerTeam() {
		for (final UHCTeam team : getLivingTeams()) {
			for (final UHCPlayer member : team.getMembers()) {
				if (member.isWinner()) {
					return team;
				}
			}
		}
		return null;
	}

	public void teleportInToGame() {
		for (final UHCTeam team : getLivingTeams()) {
			final Location location = TeleportUtils.getSafeLocation();
			for (final UHCPlayer member : team.getMembers()) {
				member.setSpawnLocation(location);
			}
			team.teleport(location);
		}
	}

	public void teleportAfterMining() {
		for (final UHCTeam team : getLivingTeams()) {
			final Player p = team.getLeader().getPlayer();
			final Location location = p.getLocation();

			final double y = location.getWorld().getHighestBlockYAt(location);
			location.setY(y);

			team.teleport(location);
		}
	}

	private UHCTeam findFreeTeam() {
		final List<UHCTeam> freeTeams = teamsList.stream().filter(team -> !team.isFull()).collect(Collectors.toList());
		final UHCTeam emptyTeam = freeTeams.stream().filter(team -> team.getMembers().isEmpty()).findFirst().orElse(null);
		if (emptyTeam == null) return freeTeams.stream().findFirst().orElse(null);
		return emptyTeam;
	}

	public boolean exists(final String teamName) {
		return teamsList.contains(getTeam(teamName));
	}

	public void onDisable() {
		this.teamsList.clear();
	}

}