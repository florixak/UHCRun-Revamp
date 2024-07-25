package me.florixak.uhcrevamp.game.kits;

public class KitSelectionInfo {
    private final Kit selectedKit;

    public KitSelectionInfo(Kit selectedKit) {
        this.selectedKit = selectedKit;
    }

    public Kit getSelectedKit() {
        return selectedKit;
    }

    public double getCost() {
        return selectedKit.getCost();
    }
}
