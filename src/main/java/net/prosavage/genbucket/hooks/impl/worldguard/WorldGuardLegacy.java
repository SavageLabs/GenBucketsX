package net.prosavage.genbucket.hooks.impl.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.prosavage.genbucket.hooks.impl.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class WorldGuardLegacy extends WorldGuardHook {

    private WorldGuardPlugin worldGuardPlugin;

    @Override
    public boolean canBuild(Player player, Block block) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(block.getLocation());
        if (!hasBypass(player, block.getLocation())) {
            return query.testState(loc, worldGuardPlugin.wrapPlayer(player), Flags.BUILD);
        }else {
            return true;
        }
    }

    public boolean hasBypass(Player p, Location loc) {
        if (worldGuardPlugin == null) {
            worldGuardPlugin = WorldGuardPlugin.inst();
        }
        return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(worldGuardPlugin.wrapPlayer(p), BukkitAdapter.adapt(loc.getWorld()));
    }

}
