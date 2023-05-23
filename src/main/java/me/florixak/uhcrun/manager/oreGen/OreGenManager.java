package me.florixak.uhcrun.manager.oreGen;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.OreGeneratorUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class OreGenManager {

    private GameManager gameManager;
    private FileConfiguration ore_gen_config;

    private List<Material> notAllowed;
    private List<OreGen> oreGenList;

    public OreGenManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.ore_gen_config = gameManager.getConfigManager().getFile(ConfigType.ORE_GENERATION).getConfig();

        this.oreGenList = new ArrayList<>();
    }

    public void loadOres() {
        if (ore_gen_config.getConfigurationSection("ore-generation") == null
                || ore_gen_config.getConfigurationSection("ore-generation").getKeys(false) == null) {
            return;
        }

        for (String materialN : ore_gen_config.getConfigurationSection("ore-generation").getKeys(false)) {
            Material material = XMaterial.matchXMaterial(materialN).get().parseMaterial();
            int spawnAmount = ore_gen_config.getInt("ore-generation." + materialN + ".spawn-amount");
            int minVein = ore_gen_config.getInt("ore-generation." + materialN + ".min-vein");
            int maxVein = ore_gen_config.getInt("ore-generation." + materialN + ".max-vein");

            if (minVein <= 0 || maxVein <= 0) return;
            if (minVein == maxVein || maxVein < minVein) maxVein = minVein;

            OreGen oreGen = new OreGen(material, spawnAmount, minVein, maxVein);
            oreGenList.add(oreGen);
        }
    }

    private List<OreGen> getOreGens() {
        return this.oreGenList;
    }

    public void generateOres() {
        for (OreGen oreGen : getOreGens()) {
            OreGeneratorUtils.generateOre(oreGen.getMaterial(),
                    gameManager.getGameWorld(),
                    oreGen.getMinVein(),
                    oreGen.getMaxVein(),
                    oreGen.getSpawnAmount(),
                    (int) gameManager.getBorderManager().getMaxSize());
        }
    }
}
