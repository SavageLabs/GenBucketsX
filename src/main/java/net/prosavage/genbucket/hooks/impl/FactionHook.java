package net.prosavage.genbucket.hooks.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionsMCHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionsUUIDHook;
import net.prosavage.genbucket.hooks.impl.factions.SavageFactionsHook;
import net.prosavage.genbucket.utils.ChatUtils;
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
            ChatUtils.sendConsole("[SavageGenBuckets] Server Factions type has been set to MassiveCore");
            return new FactionsMCHook();
        } else if (authors.contains("ProSavage") || authors.contains("LockedThread") || authors.contains("ipodtouch0218")) {
            ChatUtils.sendConsole("[SavageGenBuckets] Server Factions type has been set to generic FactionsUUID Fork");
            return new SavageFactionsHook();
        } else {
            ChatUtils.sendConsole("[SavageGenBuckets] Server Factions type has been set to FactionsUUID");
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
