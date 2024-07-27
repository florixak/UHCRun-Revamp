package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.gui.menu.CustomRecipesMenu;
import me.florixak.uhcrevamp.gui.menu.KitsMenu;
import me.florixak.uhcrevamp.gui.menu.StatisticsMenu;
import me.florixak.uhcrevamp.gui.menu.TeamsMenu;
import me.florixak.uhcrevamp.oldGui.PerksGui;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private final GameManager gameManager;

    public InteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        ItemStack item = p.getInventory().getItemInHand();

        if (gameManager.getGameState() == GameState.LOBBY || gameManager.getGameState() == GameState.STARTING) {
            if (item == null || item.getItemMeta() == null || item.getType() == Material.AIR || item.getItemMeta().getDisplayName() == null)
                return;

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {

                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.TEAMS_TITLE))) {
                    new TeamsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.KITS_TITLE))) {
                    new KitsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.PERKS_TITLE))) {
                    new PerksGui(gameManager, uhcPlayer).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.CUSTOM_RECIPES_TITLE))) {
                    new CustomRecipesMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
                }
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(
                        TextUtils.color(GameValues.INVENTORY.STATS_TITLE))) {
                    new StatisticsMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
                }
            }
        }
    }
}