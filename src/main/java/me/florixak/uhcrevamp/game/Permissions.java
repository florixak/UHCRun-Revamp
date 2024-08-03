package me.florixak.uhcrevamp.game;

public enum Permissions {

	ANVIL("anvil"),
	FORCE_START("forcestart"),
	FORCE_SKIP("forceskip"),
	SETUP("setup"),
	WORKBENCH("workbench"),
	REVIVE("revive"),

	VIP("vip"),
	RESERVED_SLOT("reserved-slot");

	private final String PREFIX = "uhcrevamp.";
	private final String permission;

	Permissions(final String permission) {
		this.permission = PREFIX + permission;
	}

	public String getPerm() {
		return permission;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
