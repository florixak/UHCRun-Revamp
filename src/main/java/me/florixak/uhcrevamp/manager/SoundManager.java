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
		final XSound sound = getSound(GameValues.SOUNDS.STARTING_SOUND);
		playSound(sound, player, GameValues.SOUNDS.STARTING_VOLUME, GameValues.SOUNDS.STARTING_PITCH);
	}

	public void playGameStartedSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.STARTED_SOUND);
		playSound(sound, player, GameValues.SOUNDS.STARTED_VOLUME, GameValues.SOUNDS.STARTED_PITCH);
	}

	public void playPvPStarted(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.PVP_STARTED_SOUND);
		playSound(sound, player, GameValues.SOUNDS.PVP_STARTED_VOLUME, GameValues.SOUNDS.PVP_STARTED_PITCH);
	}

	public void playWinSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.WIN_SOUND);
		playSound(sound, player, GameValues.SOUNDS.WIN_VOLUME, GameValues.SOUNDS.WIN_PITCH);
	}

	public void playGameOverSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.GAME_OVER_SOUND);
		playSound(sound, player, GameValues.SOUNDS.GAME_OVER_VOLUME, GameValues.SOUNDS.GAME_OVER_PITCH);
	}

	public void playDeathmatchSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.DEATHMATCH_SOUND);
		playSound(sound, player, GameValues.SOUNDS.DEATHMATCH_VOLUME, GameValues.SOUNDS.DEATHMATCH_PITCH);
	}

	public void playDeathmatchStartingSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.DEATHMATCH_STARTING_SOUND);
		playSound(sound, player, GameValues.SOUNDS.DEATHMATCH_STARTING_VOLUME, GameValues.SOUNDS.DEATHMATCH_STARTING_PITCH);
	}

	public void playUHCLevelUpSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.UHC_LEVEL_UP_SOUND);
		playSound(sound, player, GameValues.SOUNDS.UHC_LEVEL_UP_VOLUME, GameValues.SOUNDS.UHC_LEVEL_UP_PITCH);
	}

	public void playKillSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.KILL_SOUND);
		playSound(sound, player, GameValues.SOUNDS.KILL_VOLUME, GameValues.SOUNDS.KILL_PITCH);
	}

	public void playAssistSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.ASSIST_SOUND);
		playSound(sound, player, GameValues.SOUNDS.ASSIST_VOLUME, GameValues.SOUNDS.ASSIST_PITCH);
	}

	public void playDeathSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.DEATH_SOUND);
		playSound(sound, player, GameValues.SOUNDS.DEATH_VOLUME, GameValues.SOUNDS.DEATH_PITCH);
	}

	public void playSelectBuySound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.SELECT_SOUND);
		playSound(sound, player, GameValues.SOUNDS.SELECT_VOLUME, GameValues.SOUNDS.SELECT_PITCH);
	}

	public void playPurchaseCancelSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.PURCHASE_CANCEL_SOUND);
		playSound(sound, player, GameValues.SOUNDS.PURCHASE_CANCEL_VOLUME, GameValues.SOUNDS.PURCHASE_CANCEL_PITCH);
	}

	public void playQuestCompletedSound(final Player player) {
		final XSound sound = getSound(GameValues.SOUNDS.QUEST_COMPLETE_SOUND);
		playSound(sound, player, GameValues.SOUNDS.QUEST_COMPLETE_VOLUME, GameValues.SOUNDS.QUEST_COMPLETE_PITCH);
	}
}