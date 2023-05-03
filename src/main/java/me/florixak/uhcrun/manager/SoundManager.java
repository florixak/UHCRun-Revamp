package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.utils.XSeries.XSound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoundManager {

    public void playSound(Player player, Location location, XSound sound, float volume, float pitch) {
        sound.play(player, volume, pitch);
    }

    public void playOreDestroySound(Player player) {
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.5f, 1f);
    }

    public void playStartingSound(Player player) {
        XSound.BLOCK_STONE_BUTTON_CLICK_ON.play(player, 1f, 1f);
    }

    public void playGameStarted(Player player) {
        XSound.ENTITY_PLAYER_LEVELUP.play(player, 1f, 1f);
    }

    public void playGameEnd(Player player) {
        XSound.ENTITY_PLAYER_LEVELUP.play(player, 1f, 1f);
    }

    public void playKillSound(Player player) {
        XSound.ENTITY_LIGHTNING_BOLT_IMPACT.play(player.getLocation(), 1f, 1f);
    }

    public void playWinSound(Player player) {
        XSound.ENTITY_FIREWORK_ROCKET_BLAST.play(player.getLocation(), 1f, 1f);
    }

    public void playInvOpenSound(Player player) {
        XSound.BLOCK_CHEST_OPEN.play(player, 1f, 1f);
    }

    public void playDMBegan(Player player) {
        XSound.ENTITY_ENDER_DRAGON_GROWL.play(player.getLocation(), 1f, 1f);
    }

    public void playDMStarts(Player player) {
        XSound.BLOCK_STONE_BUTTON_CLICK_ON.play(player.getLocation(), 1f, 1f);
    }

    public void playLevelUP(Player player) {
        XSound.ENTITY_PLAYER_LEVELUP.play(player, 1f, 1f);
    }
}
