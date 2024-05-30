package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

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

        new AnvilGUI.Builder()
                .onClose((slot) -> {
                    p.getInventory().addItem(slot.getLeftItem());
                })
                .onClick((slot, stateSnapshot) -> {
                    if (slot == AnvilGUI.Slot.OUTPUT && stateSnapshot.getOutputItem() != null) {
                        p.getInventory().addItem(stateSnapshot.getOutputItem());
                    }
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .plugin(UHCRun.getInstance())
                .open(p);
        return true;
    }
}
