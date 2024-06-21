package me.florixak.uhcrun.game.statistics;

public class TopStatistic {

    private String name;
    private int value;

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
