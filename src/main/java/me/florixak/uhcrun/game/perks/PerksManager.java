package me.florixak.uhcrun.game.perks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XEnchantment;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerksManager {

    private GameManager gameManager;
    private FileConfiguration perks_config;

    private List<Perk> perks;

    public PerksManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.perks_config = gameManager.getConfigManager().getFile(ConfigType.PERKS).getConfig();

        this.perks = new ArrayList<>();
    }

    public void loadPerks() {
        if (!gameManager.arePerksEnabled()) return;

        for (String kitName : perks_config.getConfigurationSection("perks").getKeys(false)) {
            List<String> options = new ArrayList<>();
            Material display_item = Material.ITEM_FRAME;
            double cost = 0;
            for (String item : perks_config.getConfigurationSection("perks." + kitName).getKeys(false)) {
                if (item.equalsIgnoreCase("display-item")) {
                    display_item = XMaterial.matchXMaterial(perks_config.getString("perks." + kitName + "." + item).toUpperCase()).get().parseMaterial();
                } else if (item.equalsIgnoreCase("cost")) {
                    cost = perks_config.getDouble("perks." + kitName + "." + item);
                } else {
                    ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem();
                    int amount = perks_config.getInt("perks." + kitName + "." + item + ".amount");

                    ItemStack newI = ItemUtils.createItem(i, null, amount, null);
                    if (perks_config.getConfigurationSection("perks." + kitName + "." + item + ".enchantments") != null) {
                        for (String enchant : perks_config.getConfigurationSection("perks." + kitName + "." + item + ".enchantments").getKeys(false)) {
                            String enchantment = enchant.toUpperCase();
                            Enchantment e = XEnchantment.matchXEnchantment(enchantment).get().getEnchant();
                            int level = perks_config.getInt("perks." + kitName + "." + item + ".enchantments." + enchantment);
                            ItemUtils.addEnchant(newI, e, level, true);
                        }
                    }
                }
            }
            Perk perk = new Perk(kitName, display_item, cost, options);
            this.perks.add(perk);
        }
    }

    public void givePerk(UHCPlayer uhcPlayer) {
        uhcPlayer.sendMessage("You chose " + uhcPlayer.getPerk() + " perk");
    }
}