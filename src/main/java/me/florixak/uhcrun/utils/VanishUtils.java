package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.UHCRun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VanishUtils implements Listener {

    public void toggleVanish(Player player) {
        Bukkit.getOnlinePlayers().forEach(pl -> pl.hidePlayer(player));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1));
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UHCRun.getInstance().getPlayerManager().getDead().forEach(hidden -> event.getPlayer().hidePlayer(hidden.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
}