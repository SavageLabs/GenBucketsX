package net.prosavage.genbucket.hooks.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionsMCHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionsUUIDHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionsXHook;
import net.prosavage.genbucket.hooks.impl.factions.SavageFactionsHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class FactionHook implements PluginHook<FactionHook> {

    @Override
    public FactionHook setup(GenBucket plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("FactionsX")) {
            ChatUtils.sendConsole(Message.PREFIX.getMessage() + "Server Factions type has been set to FactionsX");
            return new FactionsXHook();
        } else if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            List<String> authors = GenBucket.get().getServer().getPluginManager().getPlugin(getName()).getDescription().getAuthors();
            if (!authors.contains("drtshock") && !authors.contains("Benzimmer")) {
                ChatUtils.sendConsole(Message.PREFIX.getMessage() + "Server Factions type has been set to MCore Factions");
                return new FactionsMCHook();
            } else if (authors.contains("ProSavage") || authors.contains("LockedThread") || authors.contains("ipodtouch0218")) {
                ChatUtils.sendConsole(Message.PREFIX.getMessage() + "Server Factions type has been set to generic FactionsUUID Fork");
                return new SavageFactionsHook();
            } else {
                ChatUtils.sendConsole(Message.PREFIX.getMessage() + "Server Factions type has been set to FactionsUUID");
                try {
                    return new FactionsUUIDHook();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            return new SavageFactionsHook();
        }
        ChatUtils.sendConsole(Message.PREFIX.getMessage() + "No Compatible Claim Plugin found!");
        return this;
    }

    public boolean canBuild(Block block, Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return true;
    }

    public boolean hasNearbyPlayer(Player player) {
        return false;
    }

    @Override
    public String getName() {
        return "Factions";
    }

}
