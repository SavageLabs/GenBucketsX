package net.prosavage.genbucket.hooks.impl;

import com.wimbli.WorldBorder.WorldBorder;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class WorldBorderHook implements PluginHook {

    private static boolean isSetup = false;

    @Override
    public Object setup(GenBucket genBucket) {
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldBorder") != null) isSetup = true;
        return this;
    }

    @Override
    public String getName() {
        return "WorldBorder";
    }

    public static boolean isSetup() {
        return isSetup;
    }

    public static boolean isOutside(Location location) {
        try {
            if (!isSetup()) return false;
            return !WorldBorder.plugin.getWorldBorder(location.getWorld().getName()).insideBorder(location);
        } catch (NullPointerException npe) {
            return false;
        }
    }

}
