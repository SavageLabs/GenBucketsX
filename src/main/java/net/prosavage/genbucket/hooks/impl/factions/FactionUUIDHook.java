package net.prosavage.genbucket.hooks.impl.factions;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.massivecore.ps.PS;

public class FactionUUIDHook extends FactionHook {

	@Override
	public boolean canBuild(Block block, Player player) {
		if (!FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), "break", true)) {
			player.sendMessage(Message.GEN_CANT_PLACE.getMessage());
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
			if (player == otherPlayer || otherPlayer.isOp() || !player.canSee(otherPlayer) || loc.getWorld() != otherLoc.getWorld()) {
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
				player.sendMessage(Message.GEN_ENEMY_NEARBY.getMessage());
				return true;
			}
		}
		return false;
	}

}