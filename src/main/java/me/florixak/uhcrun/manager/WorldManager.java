package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.game.GameManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class WorldManager {

    private GameManager gameManager;

    public WorldManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void createNewWorld() {

        try {
            File world = new File(Bukkit.getWorldContainer(), gameManager.getGameWorld().getName());
            FileUtils.deleteDirectory(world);

            world.mkdirs();

            new File(world, "data").mkdirs();
            new File(world, "datapacks").mkdirs();
            new File(world, "playerdata").mkdirs();
            new File(world, "aoi").mkdirs();
            new File(world, "region").mkdirs();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
