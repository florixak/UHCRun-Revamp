package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.DeathmatchManager;
import me.florixak.uhcrun.manager.lobby.LobbyManager;
import me.florixak.uhcrun.manager.lobby.LobbyType;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    private GameManager gameManager;

    public SetupCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        LobbyManager lobbyM = gameManager.getLobbyManager();
        DeathmatchManager deathmatchM = gameManager.getDeathmatchManager();
        Location loc = p.getLocation();

        if (!p.hasPermission("uhcrun.setup")) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.INVALID_CMD.toString());
        }
        else if (args[0].equalsIgnoreCase("waiting-lobby")) {
            if (args.length == 1) {
                p.sendMessage(Messages.INVALID_CMD.toString());
            } else if (args[1].equalsIgnoreCase("set")) {
                lobbyM.setLobby(LobbyType.WAITING, loc);
                p.sendMessage("Waiting lobby set!");
            } else if (args[1].contains("rem") || args[1].contains("del")) {
                lobbyM.deleteLobby(LobbyType.WAITING);
                p.sendMessage("Waiting lobby removed!");
            }
        }
        else if (args[0].equalsIgnoreCase("ending-lobby")) {
            if (args.length == 1) {
                p.sendMessage(Messages.INVALID_CMD.toString());
            } else if (args[1].equalsIgnoreCase("set")) {
                lobbyM.setLobby(LobbyType.ENDING, loc);
                p.sendMessage("Ending lobby set!");
            } else if (args[1].contains("rem") || args[1].contains("del")) {
                lobbyM.deleteLobby(LobbyType.ENDING);
                p.sendMessage("Ending lobby removed!");
            }
        }
        else if (args[0].equalsIgnoreCase("deathmatch")) {
            if (args.length == 1) {
                p.sendMessage(Messages.INVALID_CMD.toString());
            } else if (args[1].equalsIgnoreCase("set")) {
                deathmatchM.setDeathmatchLocation(loc);
                p.sendMessage("Deathmatch location was set!");
            } else if (args[1].contains("rem") || args[1].contains("del")) {
                deathmatchM.resetDeathmatchLocation();
                p.sendMessage("Deathmatch location was reset!");
            }
        }
        else {
            p.sendMessage(Messages.INVALID_CMD.toString());
        }
        return true;
    }
}