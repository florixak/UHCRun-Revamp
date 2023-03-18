package me.florixak.uhcrun.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private Player winner;

    public GameEndEvent(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return this.winner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
