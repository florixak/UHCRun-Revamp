package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class PerksManager {

    private final FileConfiguration perksConfig;
    private final List<Perk> perks;

    public PerksManager(GameManager gameManager) {
        this.perksConfig = gameManager.getConfigManager().getFile(ConfigType.PERKS).getConfig();
        this.perks = new ArrayList<>();
    }

    public void loadPerks() {
        if (!GameValues.PERKS.ENABLED) return;

        for (String perkName : perksConfig.getConfigurationSection("perks").getKeys(false)) {
            ItemStack displayItem = XMaterial.BARRIER.parseItem();
            double cost = 0;
            List<String> description = new ArrayList<>();
            List<PerkEffect> effects = new ArrayList<>();
            List<PerkItem> items = new ArrayList<>();
            List<PerkBonus> bonuses = new ArrayList<>();

            ConfigurationSection perkSection = perksConfig.getConfigurationSection("perks." + perkName);

            for (String param : perkSection.getKeys(false)) {
                if (param.equalsIgnoreCase("display-item")) {
                    displayItem = XMaterial.matchXMaterial(perkSection.getString(param)).get().parseItem();
                } else if (param.equalsIgnoreCase("cost")) {
                    cost = perkSection.getDouble(param);
                } else if (param.equalsIgnoreCase("description")) {
                    description = perkSection.getStringList(param);
                } else if (param.equalsIgnoreCase("EFFECT")) {
                    ConfigurationSection effectSection = perkSection.getConfigurationSection("EFFECT");
                    for (String effectName : effectSection.getKeys(false)) {
                        int effectDuration = effectSection.getInt(effectName + ".duration");
                        int effectAmplifier = effectSection.getInt(effectName + ".level");
                        PotionEffect effectType = XPotion.matchXPotion(effectName).get().buildPotionEffect(effectDuration, effectAmplifier);
                        PerkEffect perkEffect = new PerkEffect(effectType);
                        effects.add(perkEffect);
                    }
                } else if (param.equalsIgnoreCase("ITEM")) {
                    ConfigurationSection itemSection = perkSection.getConfigurationSection("ITEM");
                    for (String itemName : itemSection.getKeys(false)) {
                        ItemStack item = XMaterial.matchXMaterial(itemName).get().parseItem();
                        int chance = itemSection.getInt(itemName + ".chance");
                        List<Integer> amount = itemSection.getIntegerList(itemName + ".amount");
                        PerkItem perkItem = new PerkItem(item, amount, chance);
                        items.add(perkItem);
                    }
                } else if (param.equalsIgnoreCase("BONUS")) {
                    ConfigurationSection bonusSection = perkSection.getConfigurationSection("BONUS");
                    List<Double> coinsBonus = bonusSection.getDoubleList("coins");
                    List<Double> uhcExpBonus = bonusSection.getDoubleList("uhc-exp");
                    List<Integer> expBonus = bonusSection.getIntegerList("exp");
                    PerkBonus perkBonus = new PerkBonus(coinsBonus, uhcExpBonus, expBonus);
                    bonuses.add(perkBonus);
                }
            }
            Perk perk = new Perk(perkName, perkName, displayItem, cost, description, bonuses, effects, items);
            addPerk(perk);
            Bukkit.getLogger().info(perks.size() + ". Loaded perk: " + perkName + " with cost: " + cost + " and " + effects.size() + " effects, " + items.size() + " items and " + bonuses.size() + " bonuses.");
        }
    }

    public List<Perk> getPerks() {
        return this.perks;
    }

    public void addPerk(Perk perk) {
        this.perks.add(perk);
    }

    public Perk getPerk(String name) {
        for (Perk perk : perks) {
            if (perk.getName().equalsIgnoreCase(name)) {
                return perk;
            }
        }
        return null;
    }
}