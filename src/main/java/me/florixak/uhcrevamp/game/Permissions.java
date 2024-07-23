package me.florixak.uhcrevamp.game;

public enum Permissions {

    ANVIL("anvil"),
    FORCE_START("forcestart"),
    KITS("kits"),
    SETUP("setup"),
    WORKBENCH("workbench"),
    REVIVE("revive"),

    COLOR_CHAT("color-chat"),
    RESERVED_SLOT("reserved-slot");

    private final String PREFIX = "uhcrevamp.";
    private final String permission;

    Permissions(String permission) {
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
