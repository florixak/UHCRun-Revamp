package me.florixak.uhcrun.listener.events;

import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameKillEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private UHCPlayer killer;
    private UHCPlayer victim;

    public GameKillEvent(UHCPlayer killer, UHCPlayer victim) {
        this.killer = killer;
        this.victim = victim;
    }

    public UHCPlayer getKiller() {
        return this.killer;
    }

    public UHCPlayer getVictim() {
        return this.victim;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
