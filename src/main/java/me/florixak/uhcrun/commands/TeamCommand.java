package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.teams.UHCTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private GameManager gameManager;

    public TeamCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        Gui teams_gui = gameManager.getGuiManager().getInventory("teams");

        if (args.length == 0) {
            gameManager.getGuiManager().getInventory("teams").openInv(p);
            return true;
        }

        if (gameManager.isPlaying()) {
            p.sendMessage(Messages.CANT_USE_NOW.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("leave")) {
            uhcPlayer.getTeam().leave(uhcPlayer);

        } else if (args.length == 1) {

            if (!gameManager.getTeamManager().exists(args[0])) {
                // uhcPlayer.sendMessage(Messages.TEAM_NOT_EXISTS.toString());
                teams_gui.openInv(p);
                return true;
            }

            UHCTeam team = gameManager.getTeamManager().getTeam(args[0]);
            team.join(uhcPlayer);

        } else if (args[0].equalsIgnoreCase("join")) {

            if (args.length == 1) {
                p.sendMessage("Usage: /team join *team*");
                return true;
            }

            if (!gameManager.getTeamManager().exists(args[1])) {
                uhcPlayer.sendMessage(Messages.TEAM_NOT_EXISTS.toString());
                teams_gui.openInv(p);
                return true;
            }

            UHCTeam team = gameManager.getTeamManager().getTeam(args[1]);
            team.join(uhcPlayer);
        } else {
            teams_gui.openInv(p);
        }
        return true;
    }
}
