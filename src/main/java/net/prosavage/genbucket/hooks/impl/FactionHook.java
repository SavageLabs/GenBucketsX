package net.prosavage.genbucket.hooks.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionMCHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionsUUIDHook;
import net.prosavage.genbucket.hooks.impl.factions.SavageFactionsHook;
import net.prosavage.genbucket.utils.Logger;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class FactionHook implements PluginHook<FactionHook> {

    @Override
    public FactionHook setup(GenBucket plugin) {
        List<String> authors = GenBucket.get().getServer().getPluginManager().getPlugin(getName()).getDescription().getAuthors();
        if (!authors.contains("drtshock") && !authors.contains("Benzimmer")) {
            Logger.print("Server Factions type has been set to MassiveCore", Logger.PrefixType.DEFAULT);
            return new FactionMCHook();
        } else if (authors.contains("ProSavage") || authors.contains("LockedThread") || authors.contains("ipodtouch0218")) {
            Logger.print("Server Factions type has been set to generic FactionsUUID Fork", Logger.PrefixType.DEFAULT);
            return new SavageFactionsHook();
        } else {
            Logger.print("Server Factions type has been set to FactionsUUID", Logger.PrefixType.DEFAULT);
            try {
                return new FactionsUUIDHook();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return new SavageFactionsHook();
    }

    public boolean canBuild(Block block, Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        throw new NotImplementedException("Factions does not exist!");
    }

    public boolean hasNearbyPlayer(Player player) {
        throw new NotImplementedException("Factions does not exist!");
    }

    @Override
    public String getName() {
        return "Factions";
    }

}
