package me.florixak.uhcrun.teams;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamManager {

    private final GameManager gameManager;
    private final FileConfiguration teams_config;

    private List<UHCTeam> teams;

    public TeamManager(GameManager gameManager) {
        this.gameManager = gameManager;

        this.teams_config = gameManager.getConfigManager().getFile(ConfigType.TEAMS).getConfig();
        this.teams = new ArrayList<>();
    }

    public void loadTeams() {
        if (!GameValues.TEAM_MODE) return;

        if (teams_config.contains("teams") && teams_config.getConfigurationSection("teams").getKeys(false).isEmpty()) {
            UHCRun.getInstance().getLogger().info("Team file is empty!");
            return;
        }

        for (String teamName : teams_config.getConfigurationSection("teams").getKeys(false)) {
            ItemStack display_item = XMaterial.matchXMaterial(teams_config.getString("teams." + teamName + ".display-item", "STONE")
                    .toUpperCase()).get().parseItem();
            String color = teams_config.getString("teams." + teamName + ".color");
            UHCTeam team = new UHCTeam(display_item, teamName, color, GameValues.TEAM_SIZE);
            this.teams.add(team);
        }
    }

    public UHCTeam getTeam(String name) {
        return teams.stream().filter(team -> team.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<UHCTeam> getTeams() {
        return this.teams;
    }

    public String getTeamsString() {
        return getTeams().stream().map(UHCTeam::getDisplayName).collect(Collectors.joining(", "));
    }

    public List<UHCTeam> getLivingTeams() {
        return teams.stream().filter(UHCTeam::isAlive).collect(Collectors.toList());
    }

    public void addTeam(UHCTeam team) {
        if (exists(team.getName()) || team == null) return;
        this.teams.add(team);
    }

    public void removeTeam(String teamName) {
        if (!exists(teamName) || teamName == null) return;

        List<String> teams_list = teams_config.getStringList("teams");
        teams_list.remove(teamName);

        UHCTeam team = getTeam(teamName);

        this.teams.remove(team);
        teams_config.set("teams", teams_list);
        gameManager.getConfigManager().getFile(ConfigType.TEAMS).save();
    }

    private UHCTeam findFreeTeam() {
        UHCTeam emptyTeam = null;
        for (UHCTeam team : this.teams) {
            if (team.getMembers().size() == 0) {
                emptyTeam = team;
                return emptyTeam;
            }
        }
        if (emptyTeam == null) {
            for (UHCTeam team : this.teams) {
                if (!team.isFull()) {
                    emptyTeam = team;
                }
            }
        }
        return emptyTeam;
    }

    public void joinRandomTeam(UHCPlayer uhcPlayer) {
        if (uhcPlayer.hasTeam()) return;
        findFreeTeam().addMember(uhcPlayer);
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
        return teams_config.getConfigurationSection("teams").getKeys(false).contains(teamName);
    }

    public void onDisable() {
        this.teams.clear();
    }

}