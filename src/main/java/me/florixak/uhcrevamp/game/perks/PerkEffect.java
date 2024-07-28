package me.florixak.uhcrevamp.game.perks;

import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.potion.PotionEffect;

public class PerkEffect {

    private final String displayName;
    private final PotionEffect effectType;

    public PerkEffect(PotionEffect effectType) {
        this.effectType = effectType;
        this.displayName = getEffectName() + " " + getAmplifier() + " (" + getDuration() + "s)";
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
        return effectType.getAmplifier() + 1;
    }

    public void giveEffect(UHCPlayer uhcPlayer) {
        uhcPlayer.addEffect(XPotion.matchXPotion(effectType.getType()), effectType.getAmplifier(), effectType.getDuration());
        uhcPlayer.sendMessage(TextUtils.color("&aYou have received a perk effect! &e" + getDisplayName() + " &afor &6" + getDuration() + " &aseconds."));
    }
}
