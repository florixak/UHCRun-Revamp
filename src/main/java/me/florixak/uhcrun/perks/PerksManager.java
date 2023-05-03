package me.florixak.uhcrun.perks;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.xseries.XPotion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PerksManager {

    private GameManager gameManager;

    public PerksManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void givePerk(UHCPlayer uhcPlayer) {

        Player p = uhcPlayer.getPlayer();
        uhcPlayer.sendMessage("You chose " + uhcPlayer.getPerk() + " perk");

        switch (uhcPlayer.getPerk()) {
            case NONE:
                break;
            case STRENGTH:
                p.addPotionEffect(XPotion.INCREASE_DAMAGE.buildPotionEffect(100, 1));
                break;
            case JUMP_BOOST:
                p.addPotionEffect(XPotion.JUMP.buildPotionEffect(200, 2));
                break;
            case REGENERATION:
                p.addPotionEffect(XPotion.REGENERATION.buildPotionEffect(100, 2));
                break;
            case SPEED:
                p.addPotionEffect(XPotion.SPEED.buildPotionEffect(100, 2));
                break;
            case FIRE_RESISTANCE:
                p.addPotionEffect(XPotion.FIRE_RESISTANCE.buildPotionEffect(100, 2));
                break;
            case ENDER_PEARL:
                p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                break;
            case RESISTANCE:
                p.addPotionEffect(XPotion.DAMAGE_RESISTANCE.buildPotionEffect(100, 2));
                break;
            case INVISIBLE:
                p.addPotionEffect(XPotion.INVISIBILITY.buildPotionEffect(100, 2));
                break;
            default:
                p.sendMessage("ERROR with perks");
                break;
        }
    }
}