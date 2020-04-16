package net.prosavage.genbucket.hooks.impl.factions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionsMCHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        return EnginePermBuild.canPlayerBuildAt(player, PS.valueOf(block), true);
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

            MPlayer other = MPlayer.get(otherPlayer);
            MPlayer local = MPlayer.get(player);
            Rel rel = other.getFaction().getRelationTo(local.getFaction());
            if (rel != Rel.ENEMY || rel != Rel.NEUTRAL) {
                continue;
            }

            double distX = Math.abs(loc.getX() - otherLoc.getX());
            double distZ = Math.abs(loc.getZ() - otherLoc.getZ());
            int radius = GenBucket.get().getConfig().getInt("radius");
            if (distX <= radius && distZ <= radius) {
                player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
                return true;
            }
        }
        return false;
    }

}