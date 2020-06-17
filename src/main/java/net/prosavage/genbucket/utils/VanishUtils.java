package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.hooks.impl.EssentialsHook;
import net.prosavage.genbucket.hooks.impl.SuperVanishHook;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class VanishUtils {

    private VanishUtils() {
        throw new AssertionError("Instantiating utility class.");
    }

    public static boolean isVanished(Player player) {
        if (player == null) return false;
        if (EssentialsHook.hasEssentials && EssentialsHook.isVanished(player)) return true;
        if (SuperVanishHook.isSetup() && SuperVanishHook.isVanished(player)) return true;
        return hasVanishedMetadata(player);
    }

    public static boolean isVanished(Player player, Player target) {
        if (isVanished(target)) return true;
        return (!player.canSee(target));
    }

    private static boolean hasVanishedMetadata(Player player) {
        if (player == null) return false;
        try {
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
        } catch (NullPointerException npe) {
            ChatUtils.debug("NPE while checking for vanished metadata");
            return false;
        }
    }

}
