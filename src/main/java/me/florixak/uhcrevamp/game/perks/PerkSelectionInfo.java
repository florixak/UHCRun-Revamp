package me.florixak.uhcrevamp.game.perks;

public class PerkSelectionInfo {
    private final Perk selectedPerk;

    public PerkSelectionInfo(Perk selectedPerk) {
        this.selectedPerk = selectedPerk;
    }

    public Perk getSelectedPerk() {
        return selectedPerk;
    }

    public double getCost() {
        return selectedPerk.getCost();
    }
}
