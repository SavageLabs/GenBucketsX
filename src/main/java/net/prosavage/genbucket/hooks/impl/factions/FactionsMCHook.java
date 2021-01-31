package net.prosavage.genbucket.hooks.impl.factions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
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

public class FactionsMCHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        if (!Config.HOOK_CANBUILD_CHECK.getOption()) {
            return true;
        }
        return EnginePermBuild.canPlayerBuildAt(player, PS.valueOf(block), true) || Config.HOOK_DISABLE_WILD.getOption() && isWilderness(block.getLocation());
    }

    @Override
    public boolean hasNearbyPlayer(Player player) {
        if (player == null || !Config.HOOK_NEARBY_CHECK.getOption()) {
            return false;
        }
        int radius = Config.HOOK_NEARBY_RADIUS.getInt();
        MPlayer me = MPlayer.get(player);
        if (me != null && isEnemyNear(me, radius)) {
            player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
            return true;
        }
        return false;
    }

    public boolean isWilderness(Location location) {
        return !BoardColl.get().getFactionAt(PS.valueOf(location)).isNormal();
    }

    public boolean isEnemyNear(MPlayer p, int rad) {
        List<Entity> nearby = p.getPlayer().getNearbyEntities(rad, rad, rad);
        for (Entity ent : nearby) {
            if (ent instanceof Player) {
                Player player = (Player) ent;
                // Citizens NPC.
                if (player.hasMetadata("NPC")) continue;
                MPlayer nearP = MPlayer.get(player);
                if (nearP.isOverriding() || VanishUtils.isVanished(nearP.getPlayer())) continue;
                Rel rel = nearP.getFaction().getRelationTo(p.getFaction());
                if (rel == Rel.ENEMY) return true;
            }
        }
        return false;
    }

}