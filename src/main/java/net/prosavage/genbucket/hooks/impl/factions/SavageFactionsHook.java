package net.prosavage.genbucket.hooks.impl.factions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.VanishUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

public class SavageFactionsHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        if (!GenBucket.get().getConfig().getBoolean("canbuild-check", true)) {
            return true;
        }
        try {
            if (!FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), "build", true)) {
                player.sendMessage(ChatUtils.color(Message.GEN_CANT_PLACE.getMessage()));
                return false;
            }
        } catch (NoSuchMethodError e) {
            try {
                Method canBuild = FactionsBlockListener.class.getMethod("playerCanBuildDestroyBlock", Player.class, Location.class, PermissableAction.class, boolean.class);
                boolean canBuildCheck = (boolean) canBuild.invoke(FactionsBlockListener.class, player, block.getLocation(), PermissableAction.BUILD, true);
                if (!canBuildCheck) {
                    player.sendMessage(ChatUtils.color(Message.GEN_CANT_PLACE.getMessage()));
                    return false;
                }
            } catch (Exception ex) {
                ChatUtils.debug("Exception while checking for build permissions! Notify the author of the plugin.");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasNearbyPlayer(Player player) {
        if (player == null || !GenBucket.get().getConfig().getBoolean("nearby-check", true)) {
            return false;
        }
        int radius = GenBucket.get().getConfig().getInt("radius", 32);
        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (isEnemyNear(me, radius)) {
            player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
            return true;
        }
        return false;
    }

    public boolean isEnemyNear(FPlayer p, int rad) {
        List<Entity> nearby = p.getPlayer().getNearbyEntities(rad, rad, rad);
        for (Entity ent : nearby) {
            if (ent instanceof Player) {
                FPlayer nearP = FPlayers.getInstance().getByPlayer((Player) ent);
                if (nearP.isAdminBypassing() || VanishUtils.isVanished(nearP.getPlayer())) continue;
                Relation relation = nearP.getRelationTo(p);
                if (relation.isEnemy()) return true;
            }
        }
        return false;
    }

}