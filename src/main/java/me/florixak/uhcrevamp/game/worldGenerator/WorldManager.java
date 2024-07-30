package me.florixak.uhcrevamp.game.worldGenerator;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameValues;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

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
        World world = Bukkit.createWorld(worldCreator);
        if (UHCRevamp.useOldMethods) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("announceAdvancements", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
        } else {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }

    }

    private void removeWorld() {
        try {
            File world = new File(Bukkit.getWorldContainer(), GameValues.WORLD_NAME);
            FileUtils.deleteDirectory(world);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createWorld(String name) {
        if (Bukkit.getWorld(name) != null) return;
        WorldCreator c = new WorldCreator(name);
        World world = c.createWorld();
    }
}
