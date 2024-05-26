package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Random;

public class TeleportUtils {

    private static final GameManager gameManager = GameManager.getGameManager();

    public static HashSet<Material> bad_blocks = new HashSet<>();

    static {
        bad_blocks.add(XMaterial.LAVA.parseMaterial());
        bad_blocks.add(XMaterial.FIRE.parseMaterial());
        bad_blocks.add(XMaterial.CACTUS.parseMaterial());
        bad_blocks.add(XMaterial.WATER.parseMaterial());
        bad_blocks.add(XMaterial.DIRT.parseMaterial());
        bad_blocks.add(XMaterial.GRASS_BLOCK.parseMaterial());
        bad_blocks.add(XMaterial.STONE.parseMaterial());
    }

    public static Location generateLocation(){

        World world = GameValues.GAME_WORLD;
        double x = RandomUtils.randomDouble(0, (gameManager.getBorderManager().getSize()/2)-10);
        double y = 150.0;
        double z = RandomUtils.randomDouble(0, (gameManager.getBorderManager().getSize()/2)-10);

        Location randomLocation = new Location(world, x, y, z);
        y = randomLocation.getWorld().getHighestBlockYAt(randomLocation);
        randomLocation.setY(y);

        return randomLocation;
    }

    public static Location getSafeLocation(){

        Location randomLocation = generateLocation();

        while (!isLocationSafe(randomLocation)){
            randomLocation = generateLocation();
        }
        return randomLocation;
    }

    public static boolean isLocationSafe(Location location){

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);

        return !(bad_blocks.contains(below.getType())) || (block.getType().isSolid()) || (above.getType().isSolid());
    }

}
