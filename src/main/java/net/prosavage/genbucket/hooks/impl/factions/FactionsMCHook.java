package net.prosavage.genbucket.hooks.impl.factions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.VanishUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FactionsMCHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        if (!Config.HOOK_CANBUILD_CHECK.getOption()) {
            return true;
        }
        return EnginePermBuild.canPlayerBuildAt(player, PS.valueOf(block), true) || Config.HOOK_DISABLE_WILD.getOption() && isWilderness(block.getLocation());
    }

    @Override
    public boolean isWilderness(Location location) {
        return !BoardColl.get().getFactionAt(PS.valueOf(location)).isNormal();
    }

    @Override
    public boolean isEnemyNear(Player player, int rad) {
        MPlayer me = MPlayer.get(player);
        for (Entity ent : player.getNearbyEntities(rad, rad, rad)) {
            if (ent instanceof Player && !ent.hasMetadata("NPC")) {
                Player nearPlayer = (Player) ent;
                MPlayer nearP = MPlayer.get(nearPlayer);
                if (nearP.isOverriding() || VanishUtils.isVanished(nearPlayer)) continue;
                if (nearP.getFaction().getRelationTo(me.getFaction()) == Rel.ENEMY) return true;
            }
        }
        return false;
    }

}