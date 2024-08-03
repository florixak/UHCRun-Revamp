package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class PerksManager {

	private final FileConfiguration perksConfig;
	private final List<Perk> perks;

	public PerksManager(final GameManager gameManager) {
		this.perksConfig = gameManager.getConfigManager().getFile(ConfigType.PERKS).getConfig();
		this.perks = new ArrayList<>();
	}

	public void loadPerks() {
		if (!GameValues.PERKS.ENABLED) return;

		for (final String perkName : perksConfig.getConfigurationSection("perks").getKeys(false)) {
			ItemStack displayItem = XMaterial.BARRIER.parseItem();
			double cost = 0;
			List<String> description = new ArrayList<>();
			final List<PerkEffect> effects = new ArrayList<>();
			final List<PerkItem> items = new ArrayList<>();
			final List<PerkBonus> bonuses = new ArrayList<>();

			final ConfigurationSection perkSection = perksConfig.getConfigurationSection("perks." + perkName);

			for (final String param : perkSection.getKeys(false)) {
				if (param.equalsIgnoreCase("display-item")) {
					displayItem = XMaterial.matchXMaterial(perkSection.getString(param)).get().parseItem();
				} else if (param.equalsIgnoreCase("cost")) {
					cost = perkSection.getDouble(param);
				} else if (param.equalsIgnoreCase("description")) {
					description = perkSection.getStringList(param);
				} else if (param.equalsIgnoreCase("EFFECT")) {
					final ConfigurationSection effectSection = perkSection.getConfigurationSection("EFFECT");
					for (final String effectName : effectSection.getKeys(false)) {
						final int effectDuration = effectSection.getInt(effectName + ".duration");
						final int effectAmplifier = effectSection.getInt(effectName + ".level");
						final PotionEffect effectType = XPotion.matchXPotion(effectName).get().buildPotionEffect(effectDuration, effectAmplifier);
						final PerkEffect perkEffect = new PerkEffect(effectType);
						effects.add(perkEffect);
					}
				} else if (param.equalsIgnoreCase("ITEM")) {
					final ConfigurationSection itemSection = perkSection.getConfigurationSection("ITEM");
					for (final String itemName : itemSection.getKeys(false)) {
						final ItemStack item = XMaterial.matchXMaterial(itemName).get().parseItem();
						final int chance = itemSection.getInt(itemName + ".chance");
						final List<Integer> amount = itemSection.getIntegerList(itemName + ".amount");
						final PerkItem perkItem = new PerkItem(item, amount, chance);
						items.add(perkItem);
					}
				} else if (param.equalsIgnoreCase("BONUS")) {
					final ConfigurationSection bonusSection = perkSection.getConfigurationSection("BONUS");
					final List<Double> coinsBonus = bonusSection.getDoubleList("coins");
					final List<Double> uhcExpBonus = bonusSection.getDoubleList("uhc-exp");
					final List<Integer> expBonus = bonusSection.getIntegerList("exp");
					final PerkBonus perkBonus = new PerkBonus(coinsBonus, uhcExpBonus, expBonus);
					bonuses.add(perkBonus);
				}
			}
			final Perk perk = new Perk(perkName, displayItem, cost, description, bonuses, effects, items);
			addPerk(perk);
//            Bukkit.getLogger().info(perks.size() + ". Loaded perk: " + perkName + " with cost: " + cost + " and " + effects.size() + " effects, " + items.size() + " items and " + bonuses.size() + " bonuses.");
		}
	}

	public List<Perk> getPerks() {
		return this.perks;
	}

	public void addPerk(final Perk perk) {
		this.perks.add(perk);
	}

	public Perk getPerk(final String name) {
		for (final Perk perk : perks) {
			if (perk.getName().equalsIgnoreCase(name)) {
				return perk;
			}
		}
		return null;
	}

	public Perk getPerk(final int index) {
		return getPerks().get(index);
	}
}