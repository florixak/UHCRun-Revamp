package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.gui.PaginatedMenu;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamsMenu extends PaginatedMenu {

    private final UHCPlayer uhcPlayer;
    private final List<UHCTeam> teamsList;

    public TeamsMenu(MenuUtils menuUtils) {
        super(menuUtils, GameValues.INVENTORY.TEAMS_TITLE);
        this.uhcPlayer = menuUtils.getUHCPlayer();
        this.teamsList = GameManager.getGameManager().getTeamManager().getTeamsList();
    }

    @Override
    public int getSlots() {
        return GameValues.INVENTORY.TEAMS_SLOTS;
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {
        if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
            close();
        } else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
            handlePaging(event, teamsList);
        } else {
            handleTeamSelection(event);
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        ItemStack item;

        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * currentPage + i;
            if (index >= teamsList.size()) break;
            if (teamsList.get(index) != null) {
                UHCTeam team = teamsList.get(i);
                List<String> lore = new ArrayList<>();
                lore.add(TextUtils.color("&7(" + team.getMembers().size() + "/" + team.getMaxSize() + ")"));
                for (UHCPlayer member : team.getMembers()) {
                    lore.add(TextUtils.color("&f" + member.getName()));
                }
                item = ItemUtils.createItem(team.getDisplayItem().getType(), "&l" + team.getDisplayName(), 1, lore);

                getInventory().setItem(i, item);
            }
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
        teamsList.get(event.getSlot()).addMember(uhcPlayer);
    }
}
