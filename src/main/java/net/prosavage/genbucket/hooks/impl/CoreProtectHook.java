package net.prosavage.genbucket.hooks.impl;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class CoreProtectHook implements PluginHook {

    private static CoreProtectAPI cpAPI;
    private static boolean isSetup = false;

    @Override
    public Object setup(GenBucket genBucket) {
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
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 6) {
            return null;
        }

        return CoreProtect;
    }

    public static void logPlacement(String playerName, Block block) {
        if (!isSetup()) return;
        cpAPI.logPlacement(playerName, block.getLocation(), block.getType(), block.getBlockData());
    }

    @Override
    public String getName() {
        return "CoreProtect";
    }

}
