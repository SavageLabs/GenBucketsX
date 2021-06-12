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
    public boolean isWilderness(Location loc) {
        return GridManager.INSTANCE.getFactionAt(loc.getChunk()).isWilderness();
    }

    @Override
    public boolean isEnemyNear(Player player, int rad) {
        try {
            FPlayer me = PlayerManager.INSTANCE.getFPlayer(player);
            for (Entity ent : player.getNearbyEntities(rad, rad, rad)) {
                if (ent instanceof Player && !ent.hasMetadata("NPC")) {
                    Player nearPlayer = (Player) ent;
                    FPlayer nearFPlayer = PlayerManager.INSTANCE.getFPlayer(nearPlayer);
                    if (nearFPlayer.getInBypass() || VanishUtils.isVanished(nearPlayer)) continue;
                    if (nearFPlayer.getFaction().getRelationTo(me.getFaction()) == Relation.ENEMY) return true;
                }
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }

}
