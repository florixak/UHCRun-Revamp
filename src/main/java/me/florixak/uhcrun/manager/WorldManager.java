package me.florixak.uhcrun.manager;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.io.IOException;

public class WorldManager {

    public void createNewWorld() {

        try {

            File wait_lobby = new File(Bukkit.getWorldContainer(), "wait-lobby");
            File world = new File(Bukkit.getWorldContainer(), "world");
            File end_lobby = new File(Bukkit.getWorldContainer(), "end-lobby");
            FileUtils.deleteDirectory(world);

            wait_lobby.mkdirs();
            world.mkdirs();
            end_lobby.mkdirs();

            new File(world, "data").mkdirs();
            new File(world, "datapacks").mkdirs();
            new File(world, "playerdata").mkdirs();
            new File(world, "aoi").mkdirs();
            new File(world, "region").mkdirs();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createLobbyWorld(String name) {
        WorldCreator c = new WorldCreator(name);
        c.type(WorldType.FLAT);
        c.generateStructures(false);
        World world = c.createWorld();
    }
}
