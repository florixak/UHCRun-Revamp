package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.utility.XSeries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SoundManager {

    private UHCRun plugin;

    public SoundManager(UHCRun plugin) {
        this.plugin = plugin;
    }

    public void playSound(Player player, Location location, Sound sound, float volume, float pitch) {
        player.playSound(location, sound, volume, pitch);
    }

    public void playOreDestroySound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
    }

    public void playStartingSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
    }

    public void playGameStarted(Player player, boolean toAll) {
        if (!toAll) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            return;
        }
        for (UUID uuid : PlayerManager.online) {
            Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }

    public void playKillSound(Player player) {
        XSound.ENTITY_LIGHTNING_BOLT_IMPACT.play(player.getLocation(), 1f, 1f);
    }

    public void playWinSound(Player player) {
        XSound.ENTITY_FIREWORK_ROCKET_BLAST.play(player.getLocation(), 1f, 1f);
    }

    public void playInvOpenSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
    }

    public void playDeathmatchBegan(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1f, 1f);
    }

    public void playDeathmatchStarts(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
    }

    public void playLevelUP(Player player) {
        XSound.ENTITY_PLAYER_LEVELUP.play(player.getLocation(), 1f, 1f);
    }
}
