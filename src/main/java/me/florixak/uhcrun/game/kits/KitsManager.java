package me.florixak.uhcrun.game.kits;

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

public class KitsManager {

    private final GameManager gameManager;
    private final FileConfiguration config, kits_config;

    private final int openWhenStartingAt;

    private List<Kit> kits;

    public KitsManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.kits_config = gameManager.getConfigManager().getFile(ConfigType.KITS).getConfig();

        this.openWhenStartingAt = config.getInt("settings.kits.open-when-starting-at");

        this.kits = new ArrayList<>();
    }

    public void loadKits() {
        if (!gameManager.areKitsEnabled()) return;

        for (String kitName : kits_config.getConfigurationSection("kits").getKeys(false)) {

            List<ItemStack> items = new ArrayList<>();
            Material display_item = XMaterial.BARRIER.parseMaterial();
            double cost = 0;

            for (String param : kits_config.getConfigurationSection("kits." + kitName).getKeys(false)) {

                if (param.equalsIgnoreCase("display-item")) {
                    display_item = XMaterial.matchXMaterial(kits_config.getString("kits." + kitName + "." + param, "BARRIER").toUpperCase()).get().parseMaterial();

                } else if (param.equalsIgnoreCase("cost")) {
                    cost = kits_config.getDouble("kits." + kitName + "." + param, 0);

                } else if (param.equalsIgnoreCase("items")) {

                    if (kits_config.getConfigurationSection("kits." + kitName + "." + param) != null) {
                        for (String item : kits_config.getConfigurationSection("kits." + kitName + "." + param).getKeys(false)) {
                            ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() != null
                                    ? XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem()
                                    : XMaterial.STONE.parseItem();

                            int amount = kits_config.getInt("kits." + kitName + "." + param + "." + item + ".amount", 1);
                            ItemStack newI = ItemUtils.createItem(i, null, amount, null);

                            if (kits_config.getConfigurationSection("kits." + kitName + "." + param + "." + item + ".enchantments") != null) {
                                for (String enchant : kits_config.getConfigurationSection("kits." + kitName + "." + param + "." + item + ".enchantments").getKeys(false)) {
                                    String enchantment = enchant.toUpperCase();
                                    Enchantment e = XEnchantment.matchXEnchantment(enchantment).get().getEnchant();
                                    int level = kits_config.getInt("kits." + kitName + "." + param + "." + item + ".enchantments." + enchantment, 1);
                                    ItemUtils.addEnchant(newI, e, level, true);
                                }
                            }
                            items.add(newI);
                        }
                    }
                }
            }
            Kit kit = new Kit(kitName, display_item, cost, items);
            addKit(kit);
        }
    }

    public void addKit(Kit kit) {
        this.kits.add(kit);
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

    public String getKitCost(String name) {
        Kit kit = getKit(name);
        return "&fCost: &e" + (kit.isFree() ? "&aFREE" : kit.getCost());
    }

    public int getOpenWhenStartingAt() {
        return this.openWhenStartingAt;
    }
    public boolean willOpenWhenStarting() {
        return this.openWhenStartingAt != -1;
    }

    public void getLobbyKit(UHCPlayer p) {

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