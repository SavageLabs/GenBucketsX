package net.prosavage.genbucket.hooks.impl;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EssentialsHook implements PluginHook {

    private static IEssentials essentials;
    public static boolean hasEssentials = false;

    @Override
    public Object setup(GenBucket genBucket) {
        essentials = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials != null) hasEssentials = true;
        return this;
    }

    public static boolean isVanished(Player player) {
        if (player == null) return false;
        if (hasEssentials && essentials != null) {
            User user = essentials.getUser(player);
            return user != null && user.isVanished();
        }
        return false;
    }

    @Override
    public String getName() {
        return "Essentials";
    }
}
