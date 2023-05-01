package me.florixak.uhcrun.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private String winner;

    public GameEndEvent(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return this.winner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
