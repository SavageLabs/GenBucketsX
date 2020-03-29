package net.prosavage.genbucket.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    private ChatUtils() {
        throw new AssertionError("Instantiating utility class.");
    }

    // Color messages
    public static void sendConsole(String str) {
        Bukkit.getConsoleSender().sendMessage(color(str));
    }

    public static String[] color(String[] str) {
        if (str.length <= 0) return str;
        List<String> colored = new ArrayList<>();
        for (String string : str) {
            colored.add(color(string));
        }
        String[] itemsArray = new String[colored.size()];
        return colored.toArray(itemsArray);
    }

    public static List<String> color(List<String> string) {
        List<String> colored = new ArrayList<>();
        for (String line : string) {
            colored.add(color(line));
        }
        return colored;
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
