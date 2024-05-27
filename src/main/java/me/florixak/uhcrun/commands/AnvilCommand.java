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
import java.util.Collections;

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
                .onClose(stateSnapshot -> {
                    stateSnapshot.getPlayer().sendMessage("You closed the inventory.");
                })
                .onClick((slot, stateSnapshot) -> { // Either use sync or async variant, not both
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if(stateSnapshot.getText().equalsIgnoreCase("you")) {
                        stateSnapshot.getPlayer().sendMessage("You have magical powers!");
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } else {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Try again"));
                    }
                })
                .preventClose()                                                    //prevents the inventory from being closed
                .text("What is the meaning of life?")                              //sets the text the GUI should start with
                .title("Enter your answer.")                                       //set the title of the GUI (only works in 1.14+)
                .plugin(UHCRun.getInstance())                                          //set the plugin instance
                .open(p);
        return true;
    }
}
