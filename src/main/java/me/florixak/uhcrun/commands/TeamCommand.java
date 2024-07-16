package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.game.gui.TeamGui;
import me.florixak.uhcrun.teams.UHCTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private final GameManager gameManager;

    public TeamCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYER.toString());
            return true;
        }

        Player p = (Player) sender;
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        if (args.length == 0) {
            new TeamGui(gameManager, uhcPlayer).open();
            return true;
        }

        if (gameManager.isPlaying()) {
            p.sendMessage(Messages.CANT_USE_NOW.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("leave")) {

            if (!uhcPlayer.hasTeam()) {
                uhcPlayer.sendMessage(Messages.TEAM_LEAVE.toString().replace("%team%", uhcPlayer.getTeam().getDisplayName()));
                return true;
            }
            uhcPlayer.getTeam().removeMember(uhcPlayer);

        } else if (args.length == 1) {

            if (!gameManager.getTeamManager().exists(args[0])) {
                new TeamGui(gameManager, uhcPlayer).open();
                return true;
            }

            UHCTeam team = gameManager.getTeamManager().getTeam(args[0]);
            team.addMember(uhcPlayer);

        } else if (args[0].equalsIgnoreCase("join")) {

            if (args.length == 1) {
                p.sendMessage(Messages.INVALID_CMD.toString());
                return true;
            }

            if (!gameManager.getTeamManager().exists(args[1])) {
                new TeamGui(gameManager, uhcPlayer).open();
                return true;
            }

            UHCTeam team = gameManager.getTeamManager().getTeam(args[1]);
            team.addMember(uhcPlayer);
        } else {
            new TeamGui(gameManager, uhcPlayer).open();
        }
        return true;
    }
}
