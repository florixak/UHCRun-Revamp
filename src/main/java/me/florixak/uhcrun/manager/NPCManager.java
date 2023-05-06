package me.florixak.uhcrun.manager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCManager {

    private GameManager gameManager;

    public NPCManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void spawn(Player player) {

        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(player.getUniqueId());
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();

        // NPC textures
        Property textures = (Property) craftPlayer.getBukkitEntity().getProfile()
                .getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), uhcPlayer.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));

        // Creating NPC
        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(),
                gameProfile);
        Location loc = player.getLocation();
        entityPlayer.forceSetPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        entityPlayer.getBukkitEntity().setBedSpawnLocation(loc);

        // Fake bed
        Location bed = loc.add(1, 0, 0);
        entityPlayer.e(new BlockPosition((int) bed.getX(), (int) bed.getY(), (int) bed.getZ()));
    }




}
