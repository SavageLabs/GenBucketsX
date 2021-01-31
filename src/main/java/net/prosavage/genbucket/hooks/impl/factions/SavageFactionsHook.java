package net.prosavage.genbucket.hooks.impl.factions;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
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
        if (!Config.HOOK_CANBUILD_CHECK.getOption()) {
            return true;
        }
        try {
            if (!FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), "build", true)
                    || Config.HOOK_DISABLE_WILD.getOption() && isWilderness(block.getLocation())) {
                player.sendMessage(ChatUtils.color(Message.GEN_CANT_PLACE.getMessage()));
                ChatUtils.debug("Factions CANT_PLACE check for " + player.getName() + " ");
                return false;
            }
        } catch (NoSuchMethodError e) {
            try {
                Method canBuild = FactionsBlockListener.class.getMethod("playerCanBuildDestroyBlock", Player.class, Location.class, PermissableAction.class, boolean.class);
                boolean canBuildCheck = (boolean) canBuild.invoke(FactionsBlockListener.class, player, block.getLocation(), PermissableAction.BUILD, true);
                if (!canBuildCheck) {
                    player.sendMessage(ChatUtils.color(Message.GEN_CANT_PLACE.getMessage()));
                    ChatUtils.debug("Factions CANT_PLACE2 check for " + player.getName() + " ");
                    return false;
                }
            } catch (Exception ex) {
                ChatUtils.debug("Exception while checking for build permissions! Notify the author of the plugin.");
                return false;
            }
        }
        return true;
    }

    public boolean isWilderness(Location location) {
        FLocation fLoc = new FLocation(location);
        return Board.getInstance().getFactionAt(fLoc).isWilderness();
    }

    @Override
    public boolean hasNearbyPlayer(Player player) {
        if (player == null || !Config.HOOK_NEARBY_CHECK.getOption()) {
            return false;
        }
        int radius = Config.HOOK_NEARBY_RADIUS.getInt();
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
                Player player = (Player) ent;
                // Citizens NPC.
                if (player.hasMetadata("NPC")) continue;
                FPlayer nearP = FPlayers.getInstance().getByPlayer(player);
                if (nearP.isAdminBypassing() || VanishUtils.isVanished(nearP.getPlayer())) continue;
                Relation relation = nearP.getRelationTo(p);
                if (relation.isEnemy()) return true;
            }
        }
        return false;
    }

}