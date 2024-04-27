package me.florixak.uhcrun.utils;

public class Permissions {

    public enum Commands {

        ANVIL("anvil"),
        FORCE_START("forcestart"),
        KITS("kits"),
        NICKNAME("nickname"),
        SETUP("setup"),
        WORKBENCH("workbench");

        private final String PREFIX = "uhcrun.";
        private String permission;

        Commands(String permission) {
            this.permission = PREFIX + permission;
        }

        public String getPermission() {
            return permission;
        }
    }
}
