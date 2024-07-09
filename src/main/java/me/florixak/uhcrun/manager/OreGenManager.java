package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.worldGenerator.OreGen;
import me.florixak.uhcrun.utils.OreGenUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class OreGenManager {

    private final GameManager gameManager;
    private final FileConfiguration oreGenConfig;

    private List<OreGen> oreGenList;

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

            OreGen oreGen = new OreGen(material, minVein, maxVein, spawnAmount);
            oreGenList.add(oreGen);
        }
    }

    private List<OreGen> getOreGens() {
        return this.oreGenList;
    }

    private boolean canSkip(Material material) {
        for (OreGen oreGen : oreGenList) {
            if (oreGen.getMaterial().equals(material)
                    || material.equals(XMaterial.STONE.parseMaterial())) {
                return true;
            }
        }
        return false;
    }

    public void generateOres() {
        loadOres();

        for (OreGen oreGen : getOreGens()) {
            OreGenUtils.generateOre(oreGen.getMaterial(),
                    GameValues.GAME_WORLD,
                    oreGen.getMinVein(),
                    oreGen.getMaxVein(),
                    oreGen.getSpawnAmount(),
                    (int) gameManager.getBorderManager().getMaxSize());
        }
    }
}
