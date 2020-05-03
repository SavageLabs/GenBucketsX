package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.GenBucket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    private ChatUtils() {
        throw new AssertionError("Instantiating utility class.");
    }

    public static void debug(String str) {
        if (GenBucket.get().getConfig().getBoolean("debug"))
            Bukkit.getConsoleSender().sendMessage(color("&e<DEBUG> &f" + str));
    }

    // Color messages
    public static void sendConsole(String str) {
        Bukkit.getConsoleSender().sendMessage(color(str));
    }

    public static String[] color(String[] str) {
        if (str == null) return new String[0];
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
        if (string == null || string.isEmpty()) return colored;
        for (String line : string) {
            colored.add(color(line));
        }
        return colored;
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
