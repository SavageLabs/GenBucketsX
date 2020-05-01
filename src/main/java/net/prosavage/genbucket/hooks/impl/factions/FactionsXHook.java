package net.prosavage.genbucket.hooks.impl.factions;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.util.Relation;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.VanishUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;


public class FactionsXHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        if (player == null || !GenBucket.get().getConfig().getBoolean("canbuild-check", true)) {
            return false;
        }
        FPlayer me = PlayerManager.INSTANCE.getFPlayer(player);
        if (!me.canBuildAt(block.getLocation())) {
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
        FPlayer me = PlayerManager.INSTANCE.getFPlayer(player);
        if (isEnemyNear(me, radius)) {
            player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
            return true;
        }
        return false;
    }


    public boolean isEnemyNear(FPlayer p, int rad) {
        List<Entity> nearby = p.getPlayer().getNearbyEntities(rad, rad, rad);
        if (nearby.isEmpty()) return false;
        for (Entity ent : nearby) {
            if (ent instanceof Player) {
                FPlayer nearP = PlayerManager.INSTANCE.getFPlayer((Player) ent);
                if (nearP.getInBypass() || VanishUtils.isVanished(nearP.getPlayer())) continue;
                return nearP.getFaction().getRelationTo(p.getFaction()) == Relation.ENEMY;
            }
        }
        return false;
    }

}
