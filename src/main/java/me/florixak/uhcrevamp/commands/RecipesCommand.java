package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.gui.menu.CustomRecipesMenu;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecipesCommand implements CommandExecutor {

	private final GameManager gameManager;

	public RecipesCommand(final GameManager gameManager) {
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

		if (args.length == 0) {
			new CustomRecipesMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
		} else if (args.length == 1) {
			final CustomRecipe recipe = gameManager.getRecipeManager().getRecipe(XMaterial.matchXMaterial(args[0]).get().parseItem());
			if (recipe == null) {
				new CustomRecipesMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
				return true;
			}
		} else {
			new CustomRecipesMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
		}
		return true;
	}
}
