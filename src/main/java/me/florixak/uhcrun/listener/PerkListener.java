package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.manager.PerksManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class PerkListener implements Listener {

    private UHCRun plugin;

    public PerkListener(UHCRun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void strengthOnKill(PlayerDeathEvent event) {

        if (event.getEntity() instanceof Player) {
            if (event.getEntity().getKiller() instanceof Player) {
                Player killer = event.getEntity().getKiller();

                if (PerksManager.haveStrength(killer)){
                    plugin.getUtilities().addPotion(killer, PotionEffectType.INCREASE_DAMAGE, 60, 1);
                }
                if (PerksManager.haveRegeneration(killer)){
                    plugin.getUtilities().addPotion(killer, PotionEffectType.REGENERATION, 200, 1);
                }
                if (PerksManager.haveResistance(killer)){
                    plugin.getUtilities().addPotion(killer, PotionEffectType.INCREASE_DAMAGE, 60, 1);
                }
                if (PerksManager.haveEnderPearl(killer)) {
                    Random ran = new Random();
                    int choice = ran.nextInt(100)+1;
                    if (choice < 41) {
                        killer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                    }
                }
                if (PerksManager.haveFireResistance(killer)) {
                    plugin.getUtilities().addPotion(killer, PotionEffectType.FIRE_RESISTANCE, 60, 1);
                }
                if (PerksManager.haveSpeed(killer)) {
                    plugin.getUtilities().addPotion(killer, PotionEffectType.SPEED, 60, 1);
                }
                if (PerksManager.haveInvisible(killer)) {
                    plugin.getUtilities().addPotion(killer, PotionEffectType.INVISIBILITY, 60, 5);
                }

            }
        }
    }
}