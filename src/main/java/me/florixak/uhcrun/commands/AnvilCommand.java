package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.game.Messages;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AnvilCommand implements CommandExecutor {

    private final GameManager gameManager;

    public AnvilCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission("uhcrun.anvil")) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }
        gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId()).setKit(gameManager.getKitsManager().getKit("librarian"));
        for (ItemStack item : gameManager.getKitsManager().getKit("librarian").getItems()) {
            p.getInventory().addItem(item);
        }
        return true;
    }
}
