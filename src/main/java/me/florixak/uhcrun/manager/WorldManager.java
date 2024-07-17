package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.worldGenerator.CustomBlockPopulator;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

public class WorldManager {

//    public void createNewUHCWorld() {
//        try {
//            File world = new File(Bukkit.getWorldContainer(), "world");
//            FileUtils.deleteDirectory(world);
//
//            world.mkdirs();
//
//            new File(world, "data").mkdirs();
//            new File(world, "datapacks").mkdirs();
//            new File(world, "playerdata").mkdirs();
//            new File(world, "aoi").mkdirs();
//            new File(world, "region").mkdirs();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void createNewUHCWorld() {
        removeWorld();

        WorldCreator worldCreator = new WorldCreator(GameValues.WORLD_NAME).environment(World.Environment.NORMAL);
        //worldCreator.generator(new CustomWorldGenerator());
        World world = Bukkit.createWorld(worldCreator);
        world.getPopulators().add(new CustomBlockPopulator());
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    private void removeWorld() {
        try {
            File world = new File(Bukkit.getWorldContainer(), GameValues.WORLD_NAME);
            FileUtils.deleteDirectory(world);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createWorld(String name, WorldType type, boolean genStruct) {
        if (Bukkit.getWorld(name) != null) return;
        WorldCreator c = new WorldCreator(name);
        c.type(type);
        c.generateStructures(genStruct);
        World world = c.createWorld();
    }
}
