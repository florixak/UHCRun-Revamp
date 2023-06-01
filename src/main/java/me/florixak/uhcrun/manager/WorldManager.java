package me.florixak.uhcrun.manager;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

public class WorldManager {

    public void createNewWorld() {

        try {
            File world = new File(Bukkit.getWorldContainer(), "world");
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

    public void createWorld(String name, WorldType type) {
        if (Bukkit.getWorld(name) != null) return;
        WorldCreator c = new WorldCreator(name);
        c.type(type);
        World world = c.createWorld();
    }
}
