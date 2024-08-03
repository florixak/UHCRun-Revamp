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

	public UHCCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {
		if (!(sender instanceof Player)) return true;
		final Player p = (Player) sender;
		final LobbyManager lobbyM = gameManager.getLobbyManager();
		final DeathmatchManager deathmatchM = gameManager.getDeathmatchManager();
		final Location loc = p.getLocation();

		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			if (p.hasPermission(SETUP.getPerm())) {
				for (final String message : Messages.UHC_ADMIN_HELP.toList()) {
					p.sendMessage(message);
				}
			} else if (p.hasPermission(VIP.getPerm())) {
				for (final String message : Messages.UHC_VIP_HELP.toList()) {
					p.sendMessage(message);
				}
			} else {
				for (final String message : Messages.UHC_PLAYER_HELP.toList()) {
					p.sendMessage(message);
				}
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("set")) {
			if (!p.hasPermission(SETUP.getPerm())) {
				p.sendMessage(Messages.NO_PERM.toString());
				return true;
			}
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
			if (!p.hasPermission(SETUP.getPerm())) {
				p.sendMessage(Messages.NO_PERM.toString());
				return true;
			}
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
//		if (args[0].equalsIgnoreCase("getperk")) {
//			if (args.length == 1) {
//				p.sendMessage(Messages.INVALID_CMD.toString());
//			} else {
//				for (final Perk perk : gameManager.getPerksManager().getPerks()) {
//					if (perk.getName().equalsIgnoreCase(args[1])) {
//						p.sendMessage(perk.getName());
//						return true;
//					}
//				}
//				p.sendMessage(gameManager.getPerksManager().getPerks().toString());
//				try {
//					gameManager.getPerksManager().getPerk(Integer.parseInt(args[1])).givePerk(gameManager.getPlayerManager().getUHCPlayer(p));
//				} catch (final Exception e) {
//					p.sendMessage("Invalid perk number");
//				}
//			}
//		}
		return true;
	}
}