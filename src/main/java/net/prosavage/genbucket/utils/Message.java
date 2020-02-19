package net.prosavage.genbucket.utils;

import org.bukkit.ChatColor;

import java.util.List;

public enum Message {

    PLUGIN_RELOAD("plugin-reload", "&aYou have reloaded the config."),
    PLAYER_REQUIRED("player-required", "&cYou need to be a player to run this command!"),
    NO_PERMISSION("no-permission", "&cYou do not have permission."),
    GEN_BLOCKED_ACTION("blocked-action", "&cYou may not shift click or place the bucket in this slot!"),
    GEN_CANCELLED("gen-cancelled", "&cYou have cancelled your generation."),
    GEN_HAS_ALREADY("gen-has-already", "&cYou already have this gen bucket!"),
    GEN_CANT_PLACE("gen-cant-place", "&cYou can't place gen buckets here!"),
    GEN_CANT_AFFORD("gen-cant-afford", "&cYou do not have sufficient funds."),
    GEN_CHARGED("gen-charged", "&cYou have been charged ${amount}"),
    GEN_ENEMY_NEARBY("gen-enemy-nearby", "&cYou cannot place gen buckets with enemies nearby!"),
    GEN_LIQUID_DISABLED("gen-liquid-disabled", "&cLiquid buckets are disabled, as the water flow causes lag. This can be bypassed in config.yml, option \"liquid-blocks\".");

    String config, message;
    String[] messages;

    Message(String config, String message) {
        this.config = config;
        this.message = message;
    }

    Message(String config, String[] messages) {
        this.config = config;
        this.messages = messages;
    }

    public String getConfig() {
        return config;
    }

    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void setMessage(String message) {
        this.message = ChatColor.translateAlternateColorCodes('&', message);
    }

    public String[] getMessages() {
        return this.messages;
    }

    public void setMessages(List<String> list) {
        this.messages = list.stream().toArray(String[]::new);
    }

}