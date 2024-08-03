package me.florixak.uhcrevamp.game.statistics;

public class TopStatistics {

	private final String name;
	private final int value;

	public TopStatistics(final String name, final int value) {
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
