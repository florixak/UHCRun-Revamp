package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.manager.DeathmatchManager;
import me.florixak.uhcrevamp.manager.LobbyManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.florixak.uhcrevamp.game.Permissions.SETUP;
import static me.florixak.uhcrevamp.game.Permissions.VIP;

public class UHCCommand implements CommandExecutor {

    private final GameManager gameManager;

    public UHCCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        LobbyManager lobbyM = gameManager.getLobbyManager();
        DeathmatchManager deathmatchM = gameManager.getDeathmatchManager();
        Location loc = p.getLocation();

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if (p.hasPermission(SETUP.getPerm())) {
                for (String message : Messages.UHC_ADMIN_HELP.toList()) {
                    p.sendMessage(message);
                }
            } else if (p.hasPermission(VIP.getPerm())) {
                for (String message : Messages.UHC_VIP_HELP.toList()) {
                    p.sendMessage(message);
                }
            } else {
                for (String message : Messages.UHC_PLAYER_HELP.toList()) {
                    p.sendMessage(message);
                }
            }
            return true;
        }

        if (!p.hasPermission(SETUP.getPerm())) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 1) {
                p.sendMessage(Messages.INVALID_CMD.toString());
            } else if (args[1].equalsIgnoreCase("waiting-lobby")) {
                lobbyM.setWaitingLobbyLocation(loc);
                p.sendMessage(Messages.SETUP_SET_WAIT_LOBBY.toString());
            } else if (args[1].equalsIgnoreCase("ending-lobby")) {
                lobbyM.setEndingLobbyLocation(loc);
                p.sendMessage(Messages.SETUP_SET_END_LOBBY.toString());
            } else if (args[1].equalsIgnoreCase("deathmatch")) {
                deathmatchM.setDeathmatchLocation(loc);
                p.sendMessage(Messages.SETUP_SET_DEATHMATCH.toString());
            } else {
                p.sendMessage(Messages.INVALID_CMD.toString());
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1) {
                p.sendMessage(Messages.INVALID_CMD.toString());
            } else if (args[1].equalsIgnoreCase("waiting-lobby")) {
                lobbyM.removeWaitingLobby();
                p.sendMessage(Messages.SETUP_DEL_WAIT_LOBBY.toString());
            } else if (args[1].equalsIgnoreCase("ending-lobby")) {
                lobbyM.removeEndingLobby();
                p.sendMessage(Messages.SETUP_DEL_END_LOBBY.toString());
            } else if (args[1].equalsIgnoreCase("deathmatch")) {
                deathmatchM.resetDeathmatchLocation();
                p.sendMessage(Messages.SETUP_RESET_DEATHMATCH.toString());
            } else {
                p.sendMessage(Messages.INVALID_CMD.toString());
            }
        } else {
            p.sendMessage(Messages.INVALID_CMD.toString());
        }

        return true;
    }
}