package me.florixak.uhcrun.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishUtils implements Listener {

    public List<UUID> vanished = new ArrayList<>();

    public void toggleVanish(Player player) {
        if (isVanished(player)) {
            vanished.remove(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> pl.showPlayer(player));

            player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        } else {
            vanished.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> pl.hidePlayer(player));

            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1));
        }
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        vanished.forEach(hidden -> event.getPlayer().hidePlayer(Bukkit.getPlayer(hidden)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        vanished.remove(player.getUniqueId());
    }
}
