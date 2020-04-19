package net.prosavage.genbucket.hooks.impl.factions;


import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.RelationUtil;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.VanishUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class FactionsUUIDHook extends FactionHook {

    private Method playerCanBuildDestroyBlock = FactionsBlockListener.class.getMethod("playerCanBuildDestroyBlock", Player.class, Location.class, PermissibleAction.class, boolean.class);
    private Method getRelationTo = RelationUtil.class.getMethod("getRelationTo", RelationParticipator.class, RelationParticipator.class, boolean.class);

    public FactionsUUIDHook() throws NoSuchMethodException {
        //Empty
    }

    @Override
    public boolean canBuild(Block block, Player player) throws InvocationTargetException, IllegalAccessException {
        if (!(boolean) playerCanBuildDestroyBlock.invoke(FactionsBlockListener.class, player, block.getLocation(), PermissibleAction.BUILD, true)) {
            player.sendMessage(ChatUtils.color(Message.GEN_CANT_PLACE.getMessage()));
            return false;
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
        if (me != null && isEnemyNear(me, radius)) {
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
                try {
                    Relation rel = (Relation) getRelationTo.invoke(RelationUtil.class, nearP, p, false);
                    if (rel.isEnemy()) return true;
                } catch (InvocationTargetException | IllegalAccessException exception) {
                    ChatUtils.sendConsole(Message.PREFIX.getMessage() + "&eError while trying to get Relation! Contact the author! (FactionsUUIDHook)");
                    return false;
                }
            }
        }
        return false;
    }

}
