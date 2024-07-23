package me.florixak.uhcrevamp.game.teams;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamManager {

    private final GameManager gameManager;
    private final FileConfiguration teamsConfig;

    private final List<UHCTeam> teamsList;

    public TeamManager(GameManager gameManager) {
        this.gameManager = gameManager;

        this.teamsConfig = gameManager.getConfigManager().getFile(ConfigType.TEAMS).getConfig();
        this.teamsList = new ArrayList<>();
    }

    public void loadTeams() {
        if (!GameValues.TEAM.TEAM_MODE) return;

        if (teamsConfig.contains("teams") && teamsConfig.getConfigurationSection("teams").getKeys(false).isEmpty()) {
            UHCRevamp.getInstance().getLogger().info("Team file is empty!");
            return;
        }

        for (String teamName : teamsConfig.getConfigurationSection("teams").getKeys(false)) {
            ItemStack display_item = XMaterial.matchXMaterial(teamsConfig.getString("teams." + teamName + ".display-item", "BARRIER")
                    .toUpperCase()).get().parseItem();
            String color = teamsConfig.getString("teams." + teamName + ".color");
            UHCTeam team = new UHCTeam(display_item, teamName, color, GameValues.TEAM.TEAM_SIZE);
            this.teamsList.add(team);
        }
    }

    public UHCTeam getTeam(String name) {
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

    public void addTeam(UHCTeam team) {
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

    private UHCTeam findFreeTeam() {
        UHCTeam emptyTeam = null;
        for (UHCTeam team : this.teamsList) {
            if (team.getMembers().isEmpty()) {
                emptyTeam = team;
            } else if (!team.isFull()) {
                emptyTeam = team;
            } else {
                return null;
            }
        }
        return emptyTeam;
    }

    public void joinRandomTeam(UHCPlayer uhcPlayer) {
        if (uhcPlayer.hasTeam()) return;
        UHCTeam team = findFreeTeam();
        team.addMember(uhcPlayer);
    }

    public UHCTeam getWinnerTeam() {
        for (UHCTeam team : getLivingTeams()) {
            for (UHCPlayer member : team.getMembers()) {
                if (member.isWinner()) {
                    return team;
                }
            }
        }
        return null;
    }

    public void teleportAfterMining() {

        for (UHCTeam team : getLivingTeams()) {
            Player p = team.getLeader().getPlayer();
            Location location = p.getLocation();

            double y = location.getWorld().getHighestBlockYAt(location);
            location.setY(y);

            team.teleport(location);
        }
    }

    public boolean exists(String teamName) {
        return teamsList.contains(getTeam(teamName));
    }

    public void onDisable() {
        this.teamsList.clear();
    }

}