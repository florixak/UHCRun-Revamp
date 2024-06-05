package me.florixak.uhcrun.game.perks;

import com.cryptomorin.xseries.XMaterial;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PerksManager {

    private final GameManager gameManager;
    private final FileConfiguration perks_config;

    private List<Perk> perks;

    public PerksManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.perks_config = gameManager.getConfigManager().getFile(ConfigType.PERKS).getConfig();

        this.perks = new ArrayList<>();
    }

    public void loadPerks() {
        if (!GameValues.PERKS_ENABLED) return;

        for (String perkName : perks_config.getConfigurationSection("perks").getKeys(false)) {
            List<String> actions = new ArrayList<>();
            Material display_item = Material.ITEM_FRAME;
            double cost = 0;
            for (String param : perks_config.getConfigurationSection("perks." + perkName).getKeys(false)) {
                if (param.equalsIgnoreCase("display-item")) {
                    display_item = XMaterial.matchXMaterial(perks_config.getString("perks." + perkName + "." + param, "BARRIER").toUpperCase()).get().parseMaterial();
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