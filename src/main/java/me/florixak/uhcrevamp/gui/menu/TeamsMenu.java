package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.gui.Menu;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamsMenu extends Menu {

    private final UHCPlayer uhcPlayer;
    private final List<UHCTeam> teams;

    public TeamsMenu(MenuUtils menuUtils) {
        super(menuUtils);
        this.uhcPlayer = menuUtils.getUHCPlayer();
        this.teams = GameManager.getGameManager().getTeamManager().getTeamsList();
    }

    @Override
    public String getMenuName() {
        return TextUtils.color(GameValues.INVENTORY.TEAMS_TITLE);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        handleTeamSelection(event);
    }

    @Override
    public void setMenuItems() {
        ItemStack item;

        for (int i = 0; i < teams.size(); i++) {
            UHCTeam team = teams.get(i);
            List<String> lore = new ArrayList<>();
            lore.add(TextUtils.color("&7(" + team.getMembers().size() + "/" + team.getMaxSize() + ")"));
            for (UHCPlayer member : team.getMembers()) {
                lore.add(TextUtils.color("&f" + member.getName()));
            }
            item = ItemUtils.createItem(team.getDisplayItem().getType(), "&l" + team.getDisplayName(), 1, lore);

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

    private void handleTeamSelection(InventoryClickEvent event) {
        close();
        teams.get(event.getSlot()).addMember(uhcPlayer);
    }
}
