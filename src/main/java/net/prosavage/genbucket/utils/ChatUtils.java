package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.GenBucket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ChatUtils {

    private ChatUtils() {
        throw new AssertionError("Instantiating utility class.");
    }

    public static void debug(String str) {
        if (GenBucket.get().getConfig().getBoolean("debug"))
            Bukkit.getConsoleSender().sendMessage(color(Message.PREFIX.getMessage() + " &e<DEBUG> &f" + str));
    }

    public static void error(String str) {
        Bukkit.getConsoleSender().sendMessage(color(Message.PREFIX.getMessage() + " &c<ERROR> &e" + str));
    }

    public static void error(String str, Exception ex) {
        Bukkit.getLogger().log(Level.SEVERE, color(Message.PREFIX.getMessage() + " &c<ERROR> &e" + str), ex);
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
