package net.prosavage.genbucket.hooks.impl.factions;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.util.Relation;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.VanishUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionsXHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        if (!Config.HOOK_CANBUILD_CHECK.getOption()) {
            return true;
        }
        FPlayer me = PlayerManager.INSTANCE.getFPlayer(player);
        if (!me.canBuildAt(block.getLocation()) || Config.HOOK_DISABLE_WILD.getOption() && isWilderness(block.getLocation())) {
            player.sendMessage(ChatUtils.color(Message.GEN_CANT_PLACE.getMessage()));
            return false;
        }
        return true;
    }

    @Override
    public boolean hasNearbyPlayer(Player player) {
        if (player == null || !Config.HOOK_NEARBY_CHECK.getOption()) {
            return false;
        }
        int radius = Config.HOOK_NEARBY_RADIUS.getInt();
        FPlayer me = PlayerManager.INSTANCE.getFPlayer(player);
        if (isEnemyNear(me, radius)) {
            player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
            return true;
        }
        return false;
    }


    public boolean isWilderness(Location loc) {
        return GridManager.INSTANCE.getFactionAt(loc.getChunk()).isWilderness();
    }

    public boolean isEnemyNear(FPlayer p, int rad) {
        List<Entity> nearby = p.getPlayer().getNearbyEntities(rad, rad, rad);
        if (nearby.isEmpty()) return false;
        for (Entity ent : nearby) {
            if (ent instanceof Player) {
                Player player = (Player) ent;
                // Citizens NPC.
                if (player.hasMetadata("NPC")) continue;
                FPlayer nearP = PlayerManager.INSTANCE.getFPlayer(player);
                if (nearP.getInBypass() || VanishUtils.isVanished(nearP.getPlayer())) continue;
                return nearP.getFaction().getRelationTo(p.getFaction()) == Relation.ENEMY;
            }
        }
        return false;
    }

}
