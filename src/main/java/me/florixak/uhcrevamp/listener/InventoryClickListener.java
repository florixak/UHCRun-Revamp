package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.Menu;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null || isNull(event.getCurrentItem())) return;

        if (!gameManager.isPlaying() || gameManager.isEnding()) {
            event.setCancelled(true);
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            Menu menu = (Menu) holder;
            menu.handleMenu(event);
        }
    }


    private void handlePerkSelection(UHCPlayer uhcPlayer, InventoryClickEvent event) {
//        Kit selectedKit = gameManager.getKitsManager().getKitsList().get(event.getSlot());
//        uhcPlayer.getPlayer().closeInventory();
//
//        if (!GameValues.KITS.BOUGHT_FOREVER) {
//            if (!selectedKit.isFree() && uhcPlayer.getData().getMoney() < selectedKit.getCost()) {
//                uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
//                return;
//            }
//            uhcPlayer.setKit(selectedKit);
//            uhcPlayer.sendMessage(Messages.KITS_MONEY_DEDUCT_INFO.toString());
//        } else {
//            if (uhcPlayer.getData().hasKitBought(selectedKit)) {
//                uhcPlayer.setKit(selectedKit);
//            } else {
//                uhcPlayer.getPlayer().closeInventory();
//                new ConfirmGui(gameManager, uhcPlayer, selectedKit.getCost(), selectedKit, null).open();
//            }
//        }
    }


    private boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
    }
}