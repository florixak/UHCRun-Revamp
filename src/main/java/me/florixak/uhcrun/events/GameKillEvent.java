package me.florixak.uhcrun.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameKillEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private Player killer;
    private Player victim;

    public GameKillEvent(Player killer, Player victim) {
        this.killer = killer;
        this.victim = victim;
    }

    public Player getKiller() {
        return this.killer;
    }

    public Player getVictim() {
        return this.victim;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
