package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.gui.menu.QuestsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestsCommand implements CommandExecutor {

	private final GameManager gameManager;

	public QuestsCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.ONLY_PLAYER.toString());
			return true;
		}

		final Player p = (Player) sender;
		final UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

		new QuestsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
		return true;
	}
}