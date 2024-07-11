package me.florixak.uhcrun.game.kits;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XEnchantment;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitsManager {

    private final FileConfiguration config, kitsConfig;
    private List<Kit> kitsList;

    public KitsManager(GameManager gameManager) {
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.kitsConfig = gameManager.getConfigManager().getFile(ConfigType.KITS).getConfig();

        this.kitsList = new ArrayList<>();
    }

    public void loadKits() {
        if (!GameValues.KITS.ENABLED) return;

        ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
        for (String kitName : kitsSection.getKeys(false)) {

            List<ItemStack> itemsList = new ArrayList<>();
            String displayName = kitName;
            Material displayItem = XMaterial.BARRIER.parseMaterial();
            double cost = 0;

            ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitName);
            for (String param : kitSection.getKeys(false)) {

                if (param.equalsIgnoreCase("display-name")) {
                    displayName = kitSection.getString(param, kitName);

                } else if (param.equalsIgnoreCase("display-item")) {
                    displayItem = XMaterial.matchXMaterial(kitSection.getString(param, "BARRIER").toUpperCase()).get().parseMaterial();

                } else if (param.equalsIgnoreCase("cost")) {
                    cost = kitSection.getDouble(param, 0);

                } else if (param.equalsIgnoreCase("items")) {
                    itemsList = loadItems(kitSection);
                }
            }
            Kit kit = new Kit(kitName, displayName, displayItem, cost, itemsList);
            UHCRun.getInstance().getLogger().info("Name: " + kit.getName() + ", " + "Display Name: " + kit.getDisplayName() + ", Cost: " + kit.getCost() + ", Items: " + kit.getItems().toString());
            addKit(kit);
        }
    }

    private List<ItemStack> loadItems(ConfigurationSection section) {
        List<ItemStack> itemsList = new ArrayList<>();
        try {
            ConfigurationSection itemsSection = section.getConfigurationSection("items");

            if (itemsSection != null) {
                for (String item : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);
                    ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() != null ? XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() : XMaterial.STONE.parseItem();
                    int amount = itemSection.getInt("amount", 1);
                    ItemStack newI = ItemUtils.createItem(i, null, amount, null);
                    ConfigurationSection enchantsSection = itemSection.getConfigurationSection("enchantments");

                    if (enchantsSection != null) {
                        for (String enchant : enchantsSection.getKeys(false)) {
                            String enchantmentName = enchant.toUpperCase();
                            Enchantment e = XEnchantment.matchXEnchantment(enchantmentName).get().getEnchant();
                            int level = enchantsSection.getInt(enchantmentName);
                            ItemUtils.addEnchant(newI, e, level, true);
                        }
                    }
                    itemsList.add(newI);
                }
            }
        } catch (Exception e) {
            UHCRun.getInstance().getLogger().info("There is a problem with loading kit items!");
        }
        return itemsList;
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
        return GameValues.KITS.OPEN_ON_STARTING_AT;
    }

    public boolean willOpenWhenStarting() {
        return GameValues.KITS.OPEN_ON_STARTING_AT != -1;
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