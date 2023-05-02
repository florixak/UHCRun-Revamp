package me.florixak.uhcrun.kits;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XEnchantment;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitsManager {

    private GameManager gameManager;
    private FileConfiguration config, kits_config;

    private List<Kit> kits;

    public KitsManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.kits_config = gameManager.getConfigManager().getFile(ConfigType.KITS).getConfig();

        this.kits = new ArrayList<>();
    }

    public void loadKits() {
        // TODO if kits disabled

        for (String kitName : kits_config.getConfigurationSection("kits").getKeys(false)) {
            List<ItemStack> items = new ArrayList<>();
            for (String item : kits_config.getConfigurationSection("kits." + kitName).getKeys(false)) {
                ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem();
                int amount = kits_config.getInt("kits." + kitName + "." + item + ".amount");

                ItemStack newI = ItemUtils.createItem(i, null, amount, null);
                if (kits_config.getConfigurationSection("kits." + kitName + "." + item + ".enchantments") != null) {
                    for (String enchant : kits_config.getConfigurationSection("kits." + kitName + "." + item + ".enchantments").getKeys(false)) {
                        String enchantment = enchant.toUpperCase();
                        Enchantment e = XEnchantment.matchXEnchantment(enchantment).get().getEnchant();
                        int level = kits_config.getInt("kits." + kitName + "." + item + ".enchantments." + enchantment);
                        ItemUtils.addEnchant(newI, e, level, true);
                    }
                }
                items.add(newI);
            }
            Kit kit = new Kit(kitName, items);
            this.kits.add(kit);
        }
    }

    public Kit getKit(String name) {
        for (Kit kit : this.kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public List<Kit> getKits() {
        return this.kits;
    }

    public void giveKit(UHCPlayer uhcPlayer) {
        uhcPlayer.sendMessage("You chose " + uhcPlayer.getKit() + " kit");
    }

    public void getWaitingKit(UHCPlayer p) {
        for (String selector : config.getConfigurationSection("settings.selectors").getKeys(false)) {
            if (config.getBoolean("settings.selectors." + selector + ".enabled")) {
                String display_name = config.getString("settings.selectors." + selector + ".display-name");
                String material = config.getConfigurationSection("settings.selectors." + selector).getString("material").toUpperCase();
                ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
                int slot = config.getInt("settings.selectors." + selector + ".slot");

                ItemStack newItem = ItemUtils.createItem(item, display_name, 1, null);
                p.getPlayer().getInventory().setItem(slot, newItem);
            }
        }
    }
}