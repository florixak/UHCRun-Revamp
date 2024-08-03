package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.XSeries.XSound;
import org.bukkit.entity.Player;

public class SoundManager {

	public SoundManager() {
	}

	private XSound getSound(final String sound) {
		if (sound.equalsIgnoreCase("NONE") || sound == null || sound.isEmpty()) return null;
		return XSound.matchXSound(sound).orElse(null);
	}

	private void playSound(final XSound xSound, final Player player, final float volume, final float pitch) {
		if (xSound == null) return;
//		xSound.play(player.getLocation(), volume, pitch);
		player.playSound(player.getLocation(), xSound.parseSound(), volume, pitch);
	}

	public void playOreDestroySound(final Player player) {
		playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, player, 0.5f, 1f);
	}

	public void playGameStartingSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.STARTING_SOUND);
		playSound(sound, player, GameValues.Sounds.STARTING_VOLUME, GameValues.Sounds.STARTING_PITCH);
	}

	public void playGameStartedSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.STARTED_SOUND);
		playSound(sound, player, GameValues.Sounds.STARTED_VOLUME, GameValues.Sounds.STARTED_PITCH);
	}

	public void playWinSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.WIN_SOUND);
		playSound(sound, player, GameValues.Sounds.WIN_VOLUME, GameValues.Sounds.WIN_PITCH);
	}

	public void playGameEndSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.GAME_END_SOUND);
		playSound(sound, player, GameValues.Sounds.GAME_END_VOLUME, GameValues.Sounds.GAME_END_PITCH);
	}

	public void playDeathmatchSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.DEATHMATCH_SOUND);
		playSound(sound, player, GameValues.Sounds.DEATHMATCH_VOLUME, GameValues.Sounds.DEATHMATCH_PITCH);
	}

	public void playDeathmatchStartingSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.DEATHMATCH_STARTING_SOUND);
		playSound(sound, player, GameValues.Sounds.DEATHMATCH_STARTING_VOLUME, GameValues.Sounds.DEATHMATCH_STARTING_PITCH);
	}

	public void playUHCLevelUpSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.UHC_LEVEL_UP_SOUND);
		playSound(sound, player, GameValues.Sounds.UHC_LEVEL_UP_VOLUME, GameValues.Sounds.UHC_LEVEL_UP_PITCH);
	}

	public void playKillSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.KILL_SOUND);
		playSound(sound, player, GameValues.Sounds.KILL_VOLUME, GameValues.Sounds.KILL_PITCH);
	}

	public void playSelectSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.SELECT_SOUND);
		playSound(sound, player, GameValues.Sounds.SELECT_VOLUME, GameValues.Sounds.SELECT_PITCH);

	}

	public void playBuySound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.BUY_SOUND);
		playSound(sound, player, GameValues.Sounds.BUY_VOLUME, GameValues.Sounds.BUY_PITCH);
	}

	public void playPurchaseCancelSound(final Player player) {
		final XSound sound = getSound(GameValues.Sounds.PURCHASE_CANCEL_SOUND);
		playSound(sound, player, GameValues.Sounds.PURCHASE_CANCEL_VOLUME, GameValues.Sounds.PURCHASE_CANCEL_PITCH);
	}
}