package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.hooks.impl.EssentialsHook;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class VanishUtils {

    public static boolean isVanished(Player player) {
        if (EssentialsHook.hasEssentials && EssentialsHook.isVanished(player)) return true;

        if (player.hasMetadata("vanished")) {
            for (MetadataValue meta : player.getMetadata("vanished")) {
                if (meta == null) continue;
                if (meta.asBoolean()) {
                    return true;
                }
            }
        }

        return false;
    }

}
