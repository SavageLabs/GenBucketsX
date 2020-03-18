package net.prosavage.genbucket.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ChatUtils {

    private ChatUtils() {
        throw new AssertionError("Instantiating utility class.");
    }

    // Color messages
    public static void sendConsole(String str) {
        Bukkit.getConsoleSender().sendMessage(color(str));
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
