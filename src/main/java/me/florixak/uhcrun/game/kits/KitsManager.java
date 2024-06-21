package me.florixak.uhcrun.game.kits;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
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

    private final FileConfiguration config, kitsConfig;

    private final int openWhenStartingAt;

    private List<Kit> kitsList;

    public KitsManager(GameManager gameManager) {
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.kitsConfig = gameManager.getConfigManager().getFile(ConfigType.KITS).getConfig();

        this.openWhenStartingAt = config.getInt("settings.kits.open-when-starting-at");

        this.kitsList = new ArrayList<>();
    }

    public void loadKits() {
        if (!GameValues.KITS_ENABLED) return;

        for (String kit : kitsConfig.getConfigurationSection("kits").getKeys(false)) {

            List<ItemStack> items = new ArrayList<>();
            String displayName = kit;
            Material displayItem = XMaterial.BARRIER.parseMaterial();
            double cost = 0;

            for (String param : kitsConfig.getConfigurationSection("kits." + kit).getKeys(false)) {

                if (param.equalsIgnoreCase("display-name")) {
                    displayName = kitsConfig.getString("kits." + kit + "." + param, kit);

                } else if (param.equalsIgnoreCase("display-item")) {
                    displayItem = XMaterial.matchXMaterial(kitsConfig.getString("kits." + kit + "." + param, "BARRIER").toUpperCase()).get().parseMaterial();

                } else if (param.equalsIgnoreCase("cost")) {
                    cost = kitsConfig.getDouble("kits." + kit + "." + param, 0);

                } else if (param.equalsIgnoreCase("items")) {

                    if (kitsConfig.getConfigurationSection("kits." + kit + "." + param) != null) {
                        for (String item : kitsConfig.getConfigurationSection("kits." + kit + "." + param).getKeys(false)) {
                            ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() != null ? XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() : XMaterial.STONE.parseItem();

                            int amount = kitsConfig.getInt("kits." + kit + "." + param + "." + item + ".amount", 1);
                            ItemStack newI = ItemUtils.createItem(i, null, amount, null);

                            if (kitsConfig.getConfigurationSection("kits." + kit + "." + param + "." + item + ".enchantments") != null) {
                                for (String enchant : kitsConfig.getConfigurationSection("kits." + kit + "." + param + "." + item + ".enchantments").getKeys(false)) {
                                    String enchantment = enchant.toUpperCase();
                                    Enchantment e = XEnchantment.matchXEnchantment(enchantment).get().getEnchant();
                                    int level = kitsConfig.getInt("kits." + kit + "." + param + "." + item + ".enchantments." + enchantment, 1);
                                    ItemUtils.addEnchant(newI, e, level, true);
                                }
                            }
                            items.add(newI);
                        }
                    }
                }
            }
            addKit(new Kit(kit, displayName, displayItem, cost, items));
        }
    }

    public void addKit(Kit kit) {
        this.kitsList.add(kit);
    }

    public Kit getKit(String name) {
        for (Kit kit : this.kitsList) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public List<Kit> getKitsList() {
        return this.kitsList;
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
                String material = config.getConfigurationSection("settings.selectors." + selector).getString("material", "BARRIER").toUpperCase();
                ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
                int slot = config.getInt("settings.selectors." + selector + ".slot");

                ItemStack newItem = ItemUtils.createItem(item, display_name, 1, null);
                p.getPlayer().getInventory().setItem(slot, newItem);
            }
        }
    }

    public boolean exists(String kitName) {
        for (Kit kit : kitsList) {
            if (kit.getName().equalsIgnoreCase(kitName)) return true;
        }
        return false;
    }
}