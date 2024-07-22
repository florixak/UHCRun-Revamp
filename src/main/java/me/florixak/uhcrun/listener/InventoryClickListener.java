package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.gui.StatisticsGui;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.game.perks.Perk;
import me.florixak.uhcrun.game.player.UHCPlayer;
import me.florixak.uhcrun.game.teams.UHCTeam;
import me.florixak.uhcrun.utils.ItemUtils;
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

        if (event.getClickedInventory() == null || isNull(event.getCurrentItem())) return;

        Player p = (Player) event.getWhoClicked();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        String title = event.getView().getTitle();

        if (!gameManager.isPlaying() || gameManager.isEnding()) {
            event.setCancelled(true);
        }

        if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.TEAMS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying() || gameManager.isEnding()) return;
            handleTeamSelection(uhcPlayer, event);

        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.KITS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying() || gameManager.isEnding()) return;
            handleKitSelection(uhcPlayer, event);


        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.PERKS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying() || gameManager.isEnding()) return;
            handlePerkSelection(uhcPlayer, event);

        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.STATS_TITLE))) {
            event.setCancelled(true);

            handleStatistics(uhcPlayer, event);
        }
    }

    private void handleTeamSelection(UHCPlayer uhcPlayer, InventoryClickEvent event) {
        for (UHCTeam team : gameManager.getTeamManager().getTeams()) {
            if (gameManager.getTeamManager().getTeams().get(event.getSlot()) == team) {
                team.addMember(uhcPlayer);
                uhcPlayer.getPlayer().closeInventory();
            }
        }
    }

    private void handleKitSelection(UHCPlayer uhcPlayer, InventoryClickEvent event) {
        Kit selectedKit = gameManager.getKitsManager().getKitsList().get(event.getSlot());
        uhcPlayer.getPlayer().closeInventory();

        if (!GameValues.KITS.BOUGHT_FOREVER) {
            if (!selectedKit.isFree() && uhcPlayer.getData().getMoney() < selectedKit.getCost()) {
                uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
                return;
            }
            uhcPlayer.setKit(selectedKit);
            uhcPlayer.sendMessage(Messages.KITS_MONEY_DEDUCT_INFO.toString());
        } else {
            if (uhcPlayer.getData().hasKitBought(selectedKit)) {
                uhcPlayer.setKit(selectedKit);
            } else {
                uhcPlayer.getData().buyKit(selectedKit);
            }
        }
    }

    private void handlePerkSelection(UHCPlayer uhcPlayer, InventoryClickEvent event) {
        for (Perk perk : gameManager.getPerksManager().getPerks()) {
            if (gameManager.getPerksManager().getPerks().get(event.getSlot()) == perk) {
                uhcPlayer.setPerk(perk);
                uhcPlayer.getPlayer().closeInventory();
            }
        }
    }

    private void handleStatistics(UHCPlayer uhcPlayer, InventoryClickEvent event) {
        if (ItemUtils.hasItemMeta(event.getCurrentItem())) {
            if (event.getRawSlot() == GameValues.STATISTICS.TOP_SLOT) {
                String displayedTop = uhcPlayer.getData().getDisplayedTop();
                List<String> displayedTops = GameValues.STATISTICS.DISPLAYED_TOPS;
                for (int i = 0; i < displayedTops.size(); i++) {
                    if (displayedTop.equalsIgnoreCase(displayedTops.get(i))) {
                        if (displayedTops.get(i).equalsIgnoreCase(displayedTops.get(displayedTops.size() - 1)))
                            uhcPlayer.getData().setDisplayedTop(displayedTops.get(0));
                        else uhcPlayer.getData().setDisplayedTop(displayedTops.get(i + 1));
                    }
                }
                uhcPlayer.getPlayer().closeInventory();
                new StatisticsGui(gameManager, uhcPlayer).open();
            }
        }
    }

    private boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
    }
}