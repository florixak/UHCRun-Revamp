package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.gui.menu.StatisticsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatisticsCommand implements CommandExecutor {

	private final GameManager gameManager;

	public StatisticsCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.ONLY_PLAYER.toString());
			return true;
		}

		final Player p = (Player) sender;
		final UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

		if (args.length == 0) {
			new StatisticsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
		}
		return true;
	}
}
