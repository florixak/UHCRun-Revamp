package me.florixak.uhcrevamp.game.quests;

abstract class Quest {

    private int id;
    private String name;
    private String description;
    private double reward;

    public Quest(int id, String name, String description, double reward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.reward = reward;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getReward() {
        return reward;
    }
}
