package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.florixak.uhcrun.game.Permissions.ANVIL;

public class AnvilCommand implements CommandExecutor {

    private final GameManager gameManager;

    public AnvilCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission(ANVIL.getPerm())) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        /*Location location = p.getLocation();
        World world = p.getWorld();
        Block block = world.getBlockAt(location);
        block.setType(Material.ANVIL);
        p.openInventory(block.getState());
        block.setType(Material.AIR);*/


        new AnvilGUI.Builder()
                .onClose(playerClose -> { //Called when the inventory is closing
                    System.out.println("The inventory is closing");
                })
                .onClick((playerComplete, text) -> { //Called when the text is submitted
                    System.out.println("The player has submitted the text: " + text);
                    return AnvilGUI.Response.close();
                })
                .text("") //Sets the default text that will be in the text box
                .itemLeft(new ItemStack(Material.AIR)) //Sets the item on the left of the GUI
                .itemRight(new ItemStack(Material.AIR)) //Sets the item on the right of the GUI
                .title("Example GUI") //Sets the title of the GUI (only works in 1.14 and above, otherwise use a plugin)
                .plugin(UHCRun.getInstance()) //Set the plugin instance
                .open(p); //Opens the GUI for the player provided

        return true;
    }
}
