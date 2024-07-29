package me.florixak.uhcrevamp.game.kits;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XEnchantment;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class KitsManager {

    private final FileConfiguration config, kitsConfig;
    private final List<Kit> kitsList;

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
            ItemStack displayItem = XMaterial.BARRIER.parseItem();
            double cost = 0;

            ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitName);
            for (String param : kitSection.getKeys(false)) {

                if (param.equalsIgnoreCase("display-item")) {
                    displayItem = XMaterial.matchXMaterial(kitSection.getString(param, "BARRIER").toUpperCase()).get().parseItem();

                } else if (param.equalsIgnoreCase("cost")) {
                    cost = kitSection.getDouble(param, 0);

                } else if (param.equalsIgnoreCase("items")) {
                    itemsList = loadItems(kitSection);
                }
            }
            Kit kit = new Kit(kitName, kitName, displayItem, cost, itemsList);
            addKit(kit);
        }
    }

    private List<ItemStack> loadItems(ConfigurationSection section) {
        List<ItemStack> itemsList = new ArrayList<>();
        try {
            ConfigurationSection itemsSection = section.getConfigurationSection("items");
            if (itemsSection != null && !itemsSection.getKeys(false).isEmpty()) {
                for (String item : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);
                    ItemStack i = XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() != null ? XMaterial.matchXMaterial(item.toUpperCase()).get().parseItem() : XMaterial.STONE.parseItem();
                    int amount = itemSection.getInt("amount", 1);
                    ItemStack newI = ItemUtils.createItem(i.getType(), null, amount, null);
                    ConfigurationSection enchantsSection = itemSection.getConfigurationSection("enchantments");
                    if (enchantsSection != null) {
                        for (String enchant : enchantsSection.getKeys(false)) {
                            String enchantmentName = enchant.toUpperCase();
                            Enchantment e = XEnchantment.matchXEnchantment(enchantmentName).get().getEnchant();
                            int level = enchantsSection.getInt(enchantmentName);
                            ItemUtils.addEnchant(newI, e, level);
                        }
                    }
                    itemsList.add(newI);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().info("There is a problem with loading kit items!");
        }

        try {
            ConfigurationSection potionsSection = section.getConfigurationSection("potions");
            if (potionsSection != null && !potionsSection.getKeys(false).isEmpty()) {
                for (String potion : potionsSection.getKeys(false)) {
                    ConfigurationSection potionSection = potionsSection.getConfigurationSection(potion);
                    int amplifier = potionSection.getInt("amplifier", 1);
                    int amount = potionSection.getInt("amount", 1);
                    int duration = potionSection.getInt("duration", 44);
                    boolean splash = potionSection.getBoolean("splash", false);
                    PotionEffectType effectType = XPotion.matchXPotion(potion).get().getPotionEffectType();
                    ItemStack potionItem = ItemUtils.createPotionItem(effectType, "", amount, duration, amplifier, splash);

                    itemsList.add(potionItem);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().info("There is a problem with loading kit potions!");
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

    public void giveLobbyKit(UHCPlayer p) {
        ConfigurationSection section = config.getConfigurationSection("settings.inventories");
        if (section == null) return;
        for (String selector : section.getKeys(false)) {
            if (!selector.matches("next|next-item|previous|previous-item|back|back-item|close|close-item")) {
                if (!section.getBoolean(selector + ".enabled")) return;
                String displayName = section.getString(selector + ".display-name");
                String material = section.getString(selector + ".display-item", "BARRIER").toUpperCase();
                ItemStack item;
                if (material.contains("HEAD") || material.contains("SKULL_ITEM")) {
                    item = Utils.getPlayerHead(p.getPlayer(), p.getName());
                } else {
                    item = XMaterial.matchXMaterial(material).get().parseItem();
                }
                int slot = section.getInt(selector + ".slot");
                ItemStack newItem = ItemUtils.createItem(item.getType(), displayName, 1, null);
                if (slot < 0 || slot > 8) return;
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