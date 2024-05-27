package me.florixak.uhcrun.game;

public enum Permissions {

    ANVIL("anvil"),
    FORCE_START("force-start"),
    KITS("kits"),
    NICKNAME("nickname"),
    SETUP("setup"),
    WORKBENCH("workbench"),

    COLOR_CHAT("color-chat"),
    RESERVED_SLOT("reserved-slot");

    private final String PREFIX = "uhcrun.";
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
