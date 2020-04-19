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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionsMCHook extends FactionHook {

    @Override
    public boolean canBuild(Block block, Player player) {
        return EnginePermBuild.canPlayerBuildAt(player, PS.valueOf(block), true);
    }

    @Override
    public boolean hasNearbyPlayer(Player player) {
        if (player == null || !GenBucket.get().getConfig().getBoolean("nearby-check", true)) {
            return false;
        }
        int radius = GenBucket.get().getConfig().getInt("radius", 32);
        MPlayer me = MPlayer.get(player);
        if (me != null && isEnemyNear(me, radius)) {
            player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
            return true;
        }
        return false;
    }

    public boolean isEnemyNear(MPlayer p, int rad) {
        List<Entity> nearby = p.getPlayer().getNearbyEntities(rad, rad, rad);
        for (Entity ent : nearby) {
            if (ent instanceof Player) {
                MPlayer nearP = MPlayer.get((Player) ent);
                if (nearP.isOverriding() || VanishUtils.isVanished(nearP.getPlayer())) continue;
                Rel rel = nearP.getFaction().getRelationTo(p.getFaction());
                if (rel == Rel.ENEMY) return true;
            }
        }
        return false;
    }

}