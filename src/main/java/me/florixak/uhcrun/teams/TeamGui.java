package me.florixak.uhcrun.teams;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamGui extends Gui {

    public TeamGui() {
        super(27, "Teams");
    }

    @Override
    public void init() {
        super.init();
        ItemStack item;
        List<UHCTeam> teams = GameManager.getGameManager().getTeamManager().getTeams();

        for (int i = 0; i < teams.size(); i++) {
            UHCTeam team = teams.get(i);
            List<String> lore = new ArrayList<>();
            lore.add(TextUtils.color("&7(" + team.getMembers().size() + "/" + team.getSize() + ")"));
            for (UHCPlayer member : team.getMembers()) {
                lore.add(TextUtils.color("&f" + member.getName()));
            }
            item = this.createItem(team.getDisplayItem().getType(), "&l" + team.getDisplayName(), lore);

            getInventory().setItem(i, item);
        }
    }

    @Override
    public void openInv(Player p) {
        if (!GameManager.getGameManager().isTeamMode()) {
            p.sendMessage("This is solo mode!");
            return;
        }
        super.openInv(p);
    }

}
