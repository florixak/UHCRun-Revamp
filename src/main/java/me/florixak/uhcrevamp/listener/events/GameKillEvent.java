package me.florixak.uhcrevamp.listener.events;

import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameKillEvent extends Event {

	public static final HandlerList handlers = new HandlerList();

	private final UHCPlayer killer;
	private final UHCPlayer victim;

	public GameKillEvent(final UHCPlayer killer, final UHCPlayer victim) {
		this.killer = killer;
		this.victim = victim;
	}

	public UHCPlayer getKiller() {
		return this.killer;
	}

	public UHCPlayer getVictim() {
		return this.victim;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
