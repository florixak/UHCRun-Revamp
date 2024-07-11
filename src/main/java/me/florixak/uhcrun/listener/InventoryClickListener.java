package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.game.perks.Perk;
import me.florixak.uhcrun.game.gui.StatisticsGui;
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

        if (!gameManager.isPlaying() || gameManager.isEnding()) {
            event.setCancelled(true);
        }

        if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.TEAMS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying() || gameManager.isEnding()) return;

            for (UHCTeam team : gameManager.getTeamManager().getTeams()) {
                if (gameManager.getTeamManager().getTeams().get(event.getSlot()) == team) {
                    team.addMember(uhcPlayer);
                    p.closeInventory();
                }
            }
        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.KITS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying() || gameManager.isEnding()) return;

            for (Kit kit : gameManager.getKitsManager().getKitsList()) {
                if (gameManager.getKitsManager().getKitsList().get(event.getSlot()) == kit) {
                    p.closeInventory();
                    if (!GameValues.KITS.BOUGHT_FOREVER) {
                        if (!kit.isFree() && uhcPlayer.getData().getMoney() < kit.getCost()) {
                            uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
                            return;
                        }
                        uhcPlayer.setKit(kit);
                        uhcPlayer.sendMessage(Messages.KITS_MONEY_DEDUCT_INFO.toString());
                    } else {
                        if (uhcPlayer.getData().alreadyBoughtKit(kit) || kit.isFree()) {
                            uhcPlayer.setKit(kit);
                        } else {
                            if (!kit.isFree() && uhcPlayer.getData().getMoney() < kit.getCost()) {
                                uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
                                return;
                            }
                            uhcPlayer.getData().buyKit(kit, kit.getCost());
                            uhcPlayer.setKit(kit);
                            uhcPlayer.sendMessage(Messages.KITS_MONEY_DEDUCT.toString()
                                    .replace("%money%", String.valueOf(uhcPlayer.getData().getMoney()))
                                    .replace("%current-money%", String.valueOf(uhcPlayer.getData().getMoney()-uhcPlayer.getKit().getCost()))
                                    .replace("%kit-cost%", String.valueOf(uhcPlayer.getKit().getCost()))
                                    .replace("%kit%", uhcPlayer.getKit().getDisplayName())
                            );
                        }
                    }
                }
            }
        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.PERKS_TITLE))) {
            event.setCancelled(true);

            if (gameManager.isPlaying() || gameManager.isEnding()) return;

            for (Perk perk : gameManager.getPerksManager().getPerks()) {
                if (gameManager.getPerksManager().getPerks().get(event.getSlot()) == perk) {
                    p.closeInventory();
                    uhcPlayer.setPerk(perk);
                }
            }
        } else if (title.equalsIgnoreCase(TextUtils.color(GameValues.INVENTORY.STATS_TITLE))) {
            event.setCancelled(true);

            if (event.getCurrentItem().getItemMeta() != null) {
                if (event.getRawSlot() == GameValues.STATISTICS.TOP_SLOT) {
                    String displayedTop = uhcPlayer.getData().getDisplayedTop();
                    List<String> displayedTops = GameValues.STATISTICS.DISPLAYED_TOPS;
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
    }

    private boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
    }
}