package net.prosavage.genbucket.hooks.impl;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class CoreProtectHook implements PluginHook<CoreProtectHook> {

    private static CoreProtectAPI cpAPI;
    private static boolean isSetup = false;

    @Override
    public CoreProtectHook setup(GenBucket genBucket) {
        try {
            cpAPI = getCoreProtect();
        } catch (Exception ex) {
            isSetup = false;
        }
        if (cpAPI != null) isSetup = true;
        return this;
    }

    public static boolean isSetup() {
        return isSetup;
    }

    private static CoreProtectAPI getCoreProtect() {
        Plugin plugin = GenBucket.get().getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !plugin.isEnabled() || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI cProtect = ((CoreProtect) plugin).getAPI();
        if (!cProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        //if (cProtect.APIVersion() < 6) {
        //    return null;
        //}

        return cProtect;
    }

    public static void logPlacement(String playerName, Block block) {
        if (!isSetup()) return;
        try {
            cpAPI.logPlacement(playerName, block.getLocation(), block.getType(), block.getBlockData());
        } catch (NoSuchMethodError e) {
            cpAPI.logPlacement(playerName, block.getLocation(), block.getType(), block.getData());
        } catch (NullPointerException e) {
            // rip
        }
    }

    @Override
    public String getName() {
        return "CoreProtect";
    }

}
