package me.florixak.uhcrevamp.game.quests;

public class KillQuest extends Quest {

    private int amount;

    public KillQuest(int id, String name, String description, double reward, int amount) {
        super(id, name, description, reward);
        this.amount = amount;
    }
}
