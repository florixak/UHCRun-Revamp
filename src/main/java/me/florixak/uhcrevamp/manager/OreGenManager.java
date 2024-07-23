package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.worldGenerator.OreGenerator;
import me.florixak.uhcrevamp.utils.OreGenUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class OreGenManager {

    private final GameManager gameManager;
    private final FileConfiguration oreGenConfig;

    private List<OreGenerator> oreGenList;

    public OreGenManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.oreGenConfig = gameManager.getConfigManager().getFile(ConfigType.ORE_GENERATION).getConfig();

        this.oreGenList = new ArrayList<>();
    }

    public void loadOres() {
        ConfigurationSection section = oreGenConfig.getConfigurationSection("ore-generation");
        if (section == null || section.getKeys(false) == null) return;

        for (String materialN : section.getKeys(false)) {
            Material matchMaterial = XMaterial.matchXMaterial(materialN.toUpperCase()).get().parseMaterial();
            Material material = matchMaterial != null ? matchMaterial : XMaterial.STONE.parseMaterial();

            if (canSkip(material)) return;

            int spawnAmount = oreGenConfig.getInt("ore-generation." + materialN + ".spawn-amount", 0);
            int minVein = oreGenConfig.getInt("ore-generation." + materialN + ".min-vein", 0);
            int maxVein = oreGenConfig.getInt("ore-generation." + materialN + ".max-vein", 0);

            if (minVein <= 0 || maxVein <= 0 || spawnAmount <= 0) return;
            if (minVein == maxVein || maxVein < minVein) maxVein = minVein;

            OreGenerator oreGen = new OreGenerator(material, minVein, maxVein, spawnAmount);
            oreGenList.add(oreGen);
        }
    }

    public List<OreGenerator> getOreGeneratorList() {
        return this.oreGenList;
    }

    private boolean canSkip(Material material) {
        for (OreGenerator oreGen : oreGenList) {
            if (oreGen.getMaterial().equals(material)
                    || material.equals(XMaterial.STONE.parseMaterial())) {
                return true;
            }
        }
        return false;
    }

    public void generateOres() {
        loadOres();

        for (OreGenerator oreGen : getOreGeneratorList()) {
            OreGenUtils.generateOre(oreGen.getMaterial(),
                    Bukkit.getWorld(GameValues.WORLD_NAME),
                    oreGen.getMinVein(),
                    oreGen.getMaxVein(),
                    oreGen.getSpawnAmount(),
                    (int) gameManager.getBorderManager().getMaxSize());
        }
    }
}
