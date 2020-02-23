package net.prosavage.genbucket.utils;

import org.bukkit.ChatColor;

public class ChatUtils {

    private ChatUtils() {
        throw new AssertionError("Instantiating utility class.");
    }

    // Color messages

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
