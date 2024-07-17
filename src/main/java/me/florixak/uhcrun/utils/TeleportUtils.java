package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;

public class TeleportUtils {

    private static final GameManager gameManager = GameManager.getGameManager();

    public static HashSet<Material> forbiddenBlocks = new HashSet<>();

    static {
        forbiddenBlocks.add(XMaterial.LAVA.parseMaterial());
        forbiddenBlocks.add(XMaterial.FIRE.parseMaterial());
        forbiddenBlocks.add(XMaterial.CACTUS.parseMaterial());
        forbiddenBlocks.add(XMaterial.WATER.parseMaterial());
        forbiddenBlocks.add(XMaterial.DIRT.parseMaterial());
        forbiddenBlocks.add(XMaterial.GRASS_BLOCK.parseMaterial());
        forbiddenBlocks.add(XMaterial.STONE.parseMaterial());
    }

    public static Location generateLocation(){

        World world = GameValues.WORLD;
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

        return !(forbiddenBlocks.contains(below.getType())) || (block.getType().isSolid()) || (above.getType().isSolid());
    }

}
