package me.florixak.uhcrun.utils.hologram;

import me.florixak.uhcrun.game.GameManager;

import java.util.ArrayList;
import java.util.List;

public class HologramManager {

    private final GameManager gameManager;
    private List<Hologram> holograms;

    public HologramManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.holograms = new ArrayList<>();
    }


}
