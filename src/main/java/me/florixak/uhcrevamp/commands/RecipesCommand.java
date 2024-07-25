package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.CustomRecipesGui;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RecipesCommand implements CommandExecutor {

    private final GameManager gameManager;

    public RecipesCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYER.toString());
            return true;
        }

        Player p = (Player) sender;
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        List<CustomRecipe> recipes = gameManager.getRecipeManager().getRecipeList();

        if (args.length == 0) {
            new CustomRecipesGui(gameManager, uhcPlayer, recipes).open();
        } else if (args.length == 1) {
            CustomRecipe recipe = gameManager.getRecipeManager().getRecipe(XMaterial.matchXMaterial(args[0]).get().parseItem());
            if (recipe == null) {
                new CustomRecipesGui(gameManager, uhcPlayer, recipes).open();
                return true;
            }
        } else {
            new CustomRecipesGui(gameManager, uhcPlayer, recipes).open();
        }
        return true;
    }
}
