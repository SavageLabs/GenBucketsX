package net.prosavage.genbucket.hooks.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import net.prosavage.genbucket.hooks.impl.worldguard.WorldGuard6;
import net.prosavage.genbucket.hooks.impl.worldguard.WorldGuard7;
import net.prosavage.genbucket.utils.Logger;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardHook implements PluginHook<WorldGuardHook> {

    @Override
    public WorldGuardHook setup(GenBucket plugin) {

        try {
            String api = GenBucket.get().getServer().getPluginManager().getPlugin(getName()).getDescription().getAPIVersion();
            Logger.print("Server WorldGuard type has been set to (Legacy)", Logger.PrefixType.DEFAULT);
            return new WorldGuard7();
        } catch (NoSuchMethodError e) {
            Logger.print("Server WorldGuard type has been set to (Default)", Logger.PrefixType.DEFAULT);
            return new WorldGuard6();
        }

    }

    public boolean canBuild(Player player, Block block) {
        throw new NotImplementedException("Factions does not exist!");
    }

    @Override
    public String getName() {
        return "WorldGuard";
    }

}
