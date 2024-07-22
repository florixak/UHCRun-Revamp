package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.Messages;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.florixak.uhcrun.game.Permissions.ANVIL;

public class AnvilCommand implements CommandExecutor {

    private final UHCRun plugin;

    public AnvilCommand(UHCRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission(ANVIL.getPerm())) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        openAnvilGUI(p);
        return true;
    }

    private void openAnvilGUI(Player p) {
        new AnvilGUI.Builder()
                .onClose(player -> {
                    // This is where you would implement your item combination logic
                    // For demonstration, let's assume player has items in both hands
                    ItemStack leftItem = p.getInventory().getItemInMainHand();
                    ItemStack rightItem = p.getInventory().getItemInOffHand();

                    if (canCombine(leftItem, rightItem)) {
                        ItemStack combinedItem = combineItems(leftItem, rightItem);
                        // Assuming combineItems handles null cases and returns null if items can't be combined
                        if (combinedItem != null) {
                            // Remove items from player's inventory
                            p.getInventory().remove(leftItem);
                            p.getInventory().remove(rightItem);
                            // Add the new or modified item
                            p.getInventory().addItem(combinedItem);
                        }
                    }
                })
                .text("Combine items") // Initial text displayed in the input field
                .itemLeft(new ItemStack(Material.AIR)) // Placeholder for the first item
                .itemRight(new ItemStack(Material.AIR)) // Placeholder for the second item
                .title("Custom Anvil") // GUI title
                .plugin(plugin) // Your plugin instance
                .open(p);
    }

    private boolean canCombine(ItemStack item1, ItemStack item2) {
        // Implement logic to check if two items can be combined
        // This is a placeholder and needs custom implementation
        return true;
    }

    private ItemStack combineItems(ItemStack item1, ItemStack item2) {
        // Implement logic to combine two items
        // This is a placeholder and needs custom implementation
        return new ItemStack(Material.DIAMOND_SWORD); // Example combined item
    }
}
