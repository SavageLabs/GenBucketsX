package net.prosavage.genbucket.hooks.impl;

import de.myzelyam.api.vanish.VanishAPI;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SuperVanishHook implements PluginHook {

    private static boolean isSetup = false;

    @Override
    public Object setup(GenBucket genBucket) {
        if (Bukkit.getServer().getPluginManager().getPlugin("SuperVanish") != null || Bukkit.getServer().getPluginManager().getPlugin("PremiumVanish") != null) isSetup = true;
        return this;
    }

    @Override
    public String getName() {
        return "PremiumVanish";
    }

    public static boolean isSetup() {
        return isSetup;
    }

    public static boolean isVanished(Player player) {
        if (!isSetup) return false;
        return VanishAPI.isInvisible(player);
    }

    public static List<UUID> getVanishedPlayers() {
        if (!isSetup) return new ArrayList<>();
        return VanishAPI.getInvisiblePlayers();
    }


}
