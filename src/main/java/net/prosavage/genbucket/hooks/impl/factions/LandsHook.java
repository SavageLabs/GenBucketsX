package net.prosavage.genbucket.hooks.impl.factions;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.player.LandPlayer;
import me.angeschossen.lands.api.role.enums.ManagementSetting;
import net.prosavage.genbucket.GenBucket;
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

public class LandsHook extends FactionHook {

    LandsIntegration landsAddon;

    public LandsHook() {
        landsAddon = new LandsIntegration(GenBucket.get(), false);
    }

    @Override
    public boolean canBuild(Block block, Player player) {
        if (!Config.HOOK_CANBUILD_CHECK.getOption()) {
            return true;
        }
        if (!landsAddon.getLand(block.getLocation()).canManagement(player, ManagementSetting.SETTING_EDIT_LAND, false) || Config.HOOK_DISABLE_WILD.getOption() && isWilderness(block.getLocation())) {
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
        LandPlayer me = landsAddon.getLandPlayer(player.getUniqueId());
        if (isEnemyNear(me, radius)) {
            player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
            return true;
        }
        return false;
    }


    public boolean isWilderness(Location loc) {
        try {
            return !landsAddon.getLand(loc).exists();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isEnemyNear(LandPlayer p, int rad) {
        List<Entity> nearby = p.getPlayer().getNearbyEntities(rad, rad, rad);
        if (nearby.isEmpty()) return false;
        for (Entity ent : nearby) {
            if (ent instanceof Player) {
                Player pEnt = (Player) ent;
                LandPlayer nearP = landsAddon.getLandPlayer(pEnt.getUniqueId());
                if (pEnt.isOp() || VanishUtils.isVanished(pEnt)) continue;
                return nearP.getLands().stream().anyMatch(p.getLands()::contains);
            }
        }
        return false;
    }

}
