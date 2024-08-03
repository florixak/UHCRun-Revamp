package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.versions.VersionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.florixak.uhcrevamp.game.Permissions.ANVIL;
import static me.florixak.uhcrevamp.game.Permissions.VIP;

public class AnvilCommand implements CommandExecutor {

	private final GameManager gameManager;

	public AnvilCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!(sender instanceof Player)) return true;

		final Player p = (Player) sender;

		if (!p.hasPermission(ANVIL.getPerm()) && !p.hasPermission(VIP.getPerm())) {
			p.sendMessage(Messages.NO_PERM.toString());
			return true;
		}

		if (!gameManager.isPlaying() || gameManager.isEnding()) {
			p.sendMessage(Messages.CANT_USE_NOW.toString());
			return true;
		}
		try {
			final VersionUtils versionUtils = UHCRevamp.getInstance().getVersionUtils();
			versionUtils.openAnvil(p);
		} catch (final NoSuchMethodError e) {
			p.sendMessage(Messages.CANT_USE_NOW.toString());
		}

		return true;
	}
}
