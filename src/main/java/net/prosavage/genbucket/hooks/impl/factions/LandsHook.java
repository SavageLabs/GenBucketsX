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
    public boolean isWilderness(Location loc) {
        try {
            return !landsAddon.getLand(loc).exists();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    @Override
    public boolean isEnemyNear(Player player, int rad) {
        LandPlayer me = landsAddon.getLandPlayer(player.getUniqueId());
        if (me == null) return false;
        for (Entity ent : player.getNearbyEntities(rad, rad, rad)) {
            if (ent instanceof Player && !ent.hasMetadata("NPC")) {
                Player pEnt = (Player) ent;
                LandPlayer nearP = landsAddon.getLandPlayer(pEnt.getUniqueId());
                if (nearP == null || pEnt.isOp() || VanishUtils.isVanished(pEnt)) continue;
                if (nearP.getLands().stream().noneMatch(me.getLands()::contains)) return true;
            }
        }
        return false;
    }

}
