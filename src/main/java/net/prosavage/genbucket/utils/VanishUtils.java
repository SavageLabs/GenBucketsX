package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.hooks.impl.EssentialsHook;
import net.prosavage.genbucket.hooks.impl.SuperVanishHook;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class VanishUtils {

    public static boolean isVanished(Player player) {
        if (EssentialsHook.hasEssentials && EssentialsHook.isVanished(player)) return true;
        if (SuperVanishHook.isSetup() && SuperVanishHook.isVanished(player)) return true;
        return hasVanishedMetadata(player);
    }

    public static boolean isVanished(Player player, Player target) {
        if (isVanished(target)) return true;
        return (!player.canSee(target));
    }

    private static boolean hasVanishedMetadata(Player player) {
        if (!player.hasMetadata("vanished")) {
            return false;
        }
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta == null) continue;
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }

}
