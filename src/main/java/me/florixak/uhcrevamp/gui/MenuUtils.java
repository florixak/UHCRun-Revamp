package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.kits.Kit;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.entity.Player;

public class MenuUtils {

    private final UHCPlayer uhcPlayer;
    private final Player owner;

    private Kit selectedKitToBuy;
    private Perk selectedPerkToBuy;
    private CustomRecipe selectedRecipe;

    public MenuUtils(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        this.owner = uhcPlayer.getPlayer();
    }

    public Player getOwner() {
        return owner;
    }

    public UHCPlayer getUHCPlayer() {
        return uhcPlayer;
    }

    public void setSelectedKitToBuy(Kit selectedKitToBuy) {
        this.selectedKitToBuy = selectedKitToBuy;
    }

    public Kit getSelectedKitToBuy() {
        return selectedKitToBuy;
    }

    public Perk getSelectedPerkToBuy() {
        return selectedPerkToBuy;
    }

    public void setSelectedPerkToBuy(Perk selectedPerkToBuy) {
        this.selectedPerkToBuy = selectedPerkToBuy;
    }

    public void setSelectedRecipe(CustomRecipe selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
    }

    public CustomRecipe getSelectedRecipe() {
        return selectedRecipe;
    }


}
