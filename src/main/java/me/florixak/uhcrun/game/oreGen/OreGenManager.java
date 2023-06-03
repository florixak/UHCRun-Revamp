package me.florixak.uhcrun.game.oreGen;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.OreGeneratorUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class OreGenManager {

    private final GameManager gameManager;
    private final FileConfiguration ore_gen_config;

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
            Material material = XMaterial.matchXMaterial(materialN.toUpperCase()).get().parseMaterial() != null
                    ? XMaterial.matchXMaterial(materialN.toUpperCase()).get().parseMaterial() : XMaterial.STONE.parseMaterial();

            if (canSkip(material)) {
                System.out.println("[POTOM SMAZAT] - " + materialN + " is doubled! skipping material");
                return;
            }

            int spawnAmount = ore_gen_config.getInt("ore-generation." + materialN + ".spawn-amount", 0);
            int minVein = ore_gen_config.getInt("ore-generation." + materialN + ".min-vein", 0);
            int maxVein = ore_gen_config.getInt("ore-generation." + materialN + ".max-vein", 0);

            if (minVein <= 0 || maxVein <= 0 || spawnAmount <= 0) return;
            if (minVein == maxVein || maxVein < minVein) maxVein = minVein;

            OreGen oreGen = new OreGen(material, spawnAmount, minVein, maxVein);
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
            OreGeneratorUtils.generateOre(oreGen.getMaterial(),
                    gameManager.getGameWorld(),
                    oreGen.getMinVein(),
                    oreGen.getMaxVein(),
                    oreGen.getSpawnAmount(),
                    (int) gameManager.getBorderManager().getMaxSize());
        }
    }
}
