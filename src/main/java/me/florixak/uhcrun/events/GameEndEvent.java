package me.florixak.uhcrun.events;

import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private UHCPlayer winner;

    public GameEndEvent(UHCPlayer winner) {
        this.winner = winner;
    }

    public UHCPlayer getWinner() {
        return this.winner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
