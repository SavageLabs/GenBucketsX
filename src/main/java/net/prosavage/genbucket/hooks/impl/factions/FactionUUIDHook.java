package net.prosavage.genbucket.hooks.impl.factions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.struct.Relation;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionUUIDHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), "build", true)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_CANT_PLACE.getMessage()));
            return false;
        }
        return true;
    }

    @Override
    public boolean hasNearbyPlayer(Player player) {
        if (!GenBucket.get().getConfig().getBoolean("nearby-check")) {
            return false;
        }
        Location loc = player.getLocation();
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            Location otherLoc = otherPlayer.getLocation();
            if (player == otherPlayer || otherPlayer.isOp() || !player.canSee(otherPlayer) || VanishUtils.isVanished(otherPlayer) || loc.getWorld() != otherLoc.getWorld()) {
                continue;
            }

            FPlayer other = FPlayers.getInstance().getByPlayer(otherPlayer);
            Relation relation = other.getRelationTo(FPlayers.getInstance().getByPlayer(player));
            if (relation.isMember() || relation.isTruce() || relation.isAlly()) {
                continue;
            }
            double distX = Math.abs(loc.getX() - otherLoc.getX());
            double distZ = Math.abs(loc.getZ() - otherLoc.getZ());
            int radius = GenBucket.get().getConfig().getInt("radius");
            if (distX <= radius && distZ <= radius) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_ENEMY_NEARBY.getMessage()));
                return true;
            }
        }
        return false;
    }

}