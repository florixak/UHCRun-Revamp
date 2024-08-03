package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.potion.PotionEffect;

public class PerkEffect {

	private final String displayName;
	private final PotionEffect effectType;

	public PerkEffect(final PotionEffect effectType) {
		this.effectType = effectType;
		this.displayName = getEffectName() + " " + getFormattedAmplifier() + " (" + getDuration() + "s)";
	}

	public PotionEffect getEffectType() {
		return effectType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEffectName() {
		return TextUtils.toNormalCamelText(XPotion.matchXPotion(effectType.getType()).name());
	}

	public int getDuration() {
		return effectType.getDuration();
	}

	public int getAmplifier() {
		return effectType.getAmplifier();
	}

	public int getFormattedAmplifier() {
		return effectType.getAmplifier() + 1;
	}

	public void giveEffect(final UHCPlayer uhcPlayer) {
		uhcPlayer.addEffect(XPotion.matchXPotion(effectType.getType()), effectType.getDuration(), getFormattedAmplifier());
		uhcPlayer.sendMessage(Messages.PERKS_EFFECT_RECEIVED.toString()
				.replace("%effect%", getDisplayName())
				.replace("%effect-duration%", String.valueOf(getDuration()))
				.replace("%effect-level%", String.valueOf(getFormattedAmplifier())));
	}
}
