package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerksManager {

    private final FileConfiguration perks_config;

    private final List<Perk> perks;

    public PerksManager(GameManager gameManager) {
        this.perks_config = gameManager.getConfigManager().getFile(ConfigType.PERKS).getConfig();

        this.perks = new ArrayList<>();
    }

    public void loadPerks() {
        if (!GameValues.PERKS.ENABLED) return;

        for (String perkName : perks_config.getConfigurationSection("perks").getKeys(false)) {
            List<String> actions = new ArrayList<>();
            ItemStack display_item = XMaterial.ITEM_FRAME.parseItem();
            double cost = 0;
            for (String param : perks_config.getConfigurationSection("perks." + perkName).getKeys(false)) {
                if (param.equalsIgnoreCase("display-item")) {
                    display_item = XMaterial.matchXMaterial(perks_config.getString("perks." + perkName + "." + param, "BARRIER").toUpperCase()).get().parseItem();
                } else if (param.equalsIgnoreCase("cost")) {
                    cost = perks_config.getDouble("perks." + perkName + "." + param, 0);
                } else if (param.equalsIgnoreCase("actions")) {
                    if (perks_config.getConfigurationSection("perks." + perkName + "." + param + ".actions") != null) {
                        for (String action : perks_config.getConfigurationSection("perks." + perkName + "." + param + ".actions").getKeys(false)) {
                            actions.add(action);
                        }
                    }
                }
            }
            Perk perk = new Perk(perkName, display_item, cost, actions);
            this.perks.add(perk);
        }
    }

    public List<Perk> getPerks() {
        return this.perks;
    }

    public void givePerk(UHCPlayer uhcPlayer) {
        uhcPlayer.sendMessage("You chose " + uhcPlayer.getPerk() + " perk");
    }
}