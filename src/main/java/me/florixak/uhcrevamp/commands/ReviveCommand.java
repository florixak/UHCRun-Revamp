package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.florixak.uhcrevamp.game.Permissions.REVIVE;

public class ReviveCommand implements CommandExecutor {

	private final GameManager gameManager;

	public ReviveCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

		if (!sender.hasPermission(REVIVE.getPerm())) {
			sender.sendMessage(Messages.NO_PERM.toString());
			return true;
		}

		if (!gameManager.isPlaying() || gameManager.isEnding()) {
			sender.sendMessage(Messages.CANT_USE_NOW.toString());
			return true;
		}

		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Messages.ONLY_PLAYER.toString());
				return true;
			}
			final UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(((Player) sender).getUniqueId());

			if (uhcPlayer.isAlive()) {
				sender.sendMessage(PlaceholderUtil.setPlaceholders(Messages.REVIVE_YOU_ALIVE.toString(), (Player) sender));
				return true;
			}
			uhcPlayer.revive();
			sender.sendMessage(PlaceholderUtil.setPlaceholders(Messages.REVIVED_YOU.toString(), (Player) sender));
		} else if (args.length == 1) {
			final UHCPlayer target = gameManager.getPlayerManager().getUHCPlayer(Bukkit.getPlayer(args[0]));
			if (!target.isOnline()) {
				sender.sendMessage(PlaceholderUtil.setPlaceholders(Messages.OFFLINE_PLAYER.toString(), target.getPlayer()));
				return true;
			}
			if (target.isAlive()) {
				sender.sendMessage(PlaceholderUtil.setPlaceholders(Messages.REVIVE_PLAYER_ALIVE.toString(), target.getPlayer()));
				return true;
			}
			target.revive();
			target.sendMessage(Messages.REVIVED_YOU.toString());
			sender.sendMessage(PlaceholderUtil.setPlaceholders(Messages.REVIVE_PLAYER_REVIVED.toString(), target.getPlayer()));
		} else {
			sender.sendMessage(Messages.INVALID_CMD.toString());
		}
		return true;
	}
}
