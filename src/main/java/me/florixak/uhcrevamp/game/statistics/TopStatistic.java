package me.florixak.uhcrevamp.game.statistics;

public class TopStatistic {

    private final String name;
    private final int value;

    public TopStatistic(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
