package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.game.perks.Perk;
import me.florixak.uhcrun.game.statistics.StatisticsGui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryClickListener implements Listener {

    private final GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null || isNull(event.getCurrentItem())) {
            return;
        }

        Player p = (Player) event.getWhoClicked();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        String title = event.getView().getTitle();

        if (!gameManager.isPlaying()) {
            event.setCancelled(true);
        }

        if (title.equalsIgnoreCase(TextUtils.color(GameValues.INV_TEAMS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying()) return;

            for (UHCTeam team : gameManager.getTeamManager().getTeams()) {
                if (gameManager.getTeamManager().getTeams().get(event.getSlot()) == team) {
                    team.addMember(uhcPlayer);
                    p.closeInventory();
                }
            }
        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INV_KITS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying()) return;

            for (Kit kit : gameManager.getKitsManager().getKits()) {
                if (gameManager.getKitsManager().getKits().get(event.getSlot()) == kit) {
                    p.closeInventory();
                    uhcPlayer.setKit(kit);
                }
            }
        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INV_PERKS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying()) return;

            for (Perk perk : gameManager.getPerksManager().getPerks()) {
                if (gameManager.getPerksManager().getPerks().get(event.getSlot()) == perk) {
                    p.closeInventory();
                    uhcPlayer.setPerk(perk);
                }
            }
        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INV_STATS_TITLE))) {
            event.setCancelled(true);

            if (event.getCurrentItem() == XMaterial.matchXMaterial(GameValues.STATS_TOP_STATS_DIS_ITEM.toUpperCase())
                    .get().parseItem()) {
                String displayedTop = uhcPlayer.getData().getDisplayedTop();
                List<String> displayedTops = GameValues.STATS_DISPLAYED_TOPS;
                for (int i = 0; i < displayedTops.size(); i++) {
                    if (displayedTop.equalsIgnoreCase(displayedTops.get(i))) {
                        if (displayedTops.get(i).equalsIgnoreCase(displayedTops.get(displayedTops.size() - 1)))
                            uhcPlayer.getData().setDisplayedTop(displayedTops.get(0));
                        else
                            uhcPlayer.getData().setDisplayedTop(displayedTops.get(i + 1));
                    }
                }
                p.closeInventory();
                new StatisticsGui(gameManager, uhcPlayer).open();
            }
        }
    }

    private boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
    }
}