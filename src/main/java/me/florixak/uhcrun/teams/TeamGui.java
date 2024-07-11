package me.florixak.uhcrun.teams;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamGui extends Gui {

    public TeamGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 3 * 9, TextUtils.color(GameValues.INVENTORY.TEAMS_TITLE));
    }

    @Override
    public void init() {
        super.init();
        ItemStack item;
        List<UHCTeam> teams = gameManager.getTeamManager().getTeams();

        for (int i = 0; i < teams.size(); i++) {
            UHCTeam team = teams.get(i);
            List<String> lore = new ArrayList<>();
            lore.add(TextUtils.color("&7(" + team.getMembers().size() + "/" + team.getMaxSize() + ")"));
            for (UHCPlayer member : team.getMembers()) {
                lore.add(TextUtils.color("&f" + member.getName()));
            }
            item = this.createItem(team.getDisplayItem().getType(), "&l" + team.getDisplayName(), lore);

            getInventory().setItem(i, item);
        }
    }

    @Override
    public void open() {
        if (!GameValues.TEAM.TEAM_MODE) {
            uhcPlayer.sendMessage("This is solo mode!");
            return;
        }
        super.open();
    }

}
