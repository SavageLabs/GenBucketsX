package net.prosavage.genbucket.hooks.impl;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import net.prosavage.genbucket.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/*
 *  WorldGuard Permission Checking.
 *  https://github.com/elBukkit/MagicPlugin/blob/master/Magic/src/main/java/com/elmakers/mine/bukkit/protection/WorldGuardAPI.java
 *  Original Authors: NathonWolf, killme
 *  Converted & Adapted: Valentina_pro
 */

public class WorldGuard {
    private Object worldGuard = null;
    private WorldGuardPlugin worldGuardPlugin = null;
    private Object regionContainer = null;
    private Method regionContainerGetMethod = null;
    private Method createQueryMethod = null;
    private Method regionQueryTestStateMethod = null;
    private Method locationAdaptMethod = null;
    private Method worldAdaptMethod = null;
    private Method regionManagerGetMethod = null;
    private Constructor<?> vectorConstructor = null;
    private Method vectorConstructorMethod = null;
    private StateFlag buildFlag;
    private StateFlag pvpFlag;
    private StateFlag exitFlag;
    private boolean initialized = false;

    private static WorldGuard instance;

    public WorldGuard() {
        instance = this;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin == null) {
            ChatUtils.debug("Could not find WorldGuard! Support will not be added.");
            return;
        }

        if (plugin instanceof WorldGuardPlugin) {
            worldGuardPlugin = (WorldGuardPlugin) plugin;

            try {
                Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
                Method getInstanceMethod = worldGuardClass.getMethod("getInstance");
                worldGuard = getInstanceMethod.invoke(null);
                ChatUtils.debug("Found WorldGuard 7+");
            } catch (Exception ex) {
                ChatUtils.debug("Found WorldGuard <7");
            }
        }
    }

    public boolean isEnabled() {
        return worldGuardPlugin != null;
    }

    public static WorldGuard getInstance() {
        return instance;
    }

    protected RegionAssociable getAssociable(Player player) {
        RegionAssociable associable;
        if (player == null) {
            associable = Associables.constant(Association.NON_MEMBER);
        } else {
            associable = worldGuardPlugin.wrapPlayer(player);
        }

        return associable;
    }

    private void initialize() {
        if (!initialized) {
            initialized = true;
            // Super hacky reflection to deal with differences in WorldGuard 6 and 7+
            if (worldGuard != null) {
                try {
                    Method getPlatFormMethod = worldGuard.getClass().getMethod("getPlatform");
                    Object platform = getPlatFormMethod.invoke(worldGuard);
                    Method getRegionContainerMethod = platform.getClass().getMethod("getRegionContainer");
                    regionContainer = getRegionContainerMethod.invoke(platform);
                    createQueryMethod = regionContainer.getClass().getMethod("createQuery");
                    Class<?> worldEditLocationClass = Class.forName("com.sk89q.worldedit.util.Location");
                    Class<?> worldEditWorldClass = Class.forName("com.sk89q.worldedit.world.World");
                    Class<?> worldEditAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
                    worldAdaptMethod = worldEditAdapterClass.getMethod("adapt", World.class);
                    locationAdaptMethod = worldEditAdapterClass.getMethod("adapt", Location.class);
                    regionContainerGetMethod = regionContainer.getClass().getMethod("get", worldEditWorldClass);
                    Class<?> regionQueryClass = Class.forName("com.sk89q.worldguard.protection.regions.RegionQuery");
                    regionQueryTestStateMethod = regionQueryClass.getMethod("testState", worldEditLocationClass, RegionAssociable.class, StateFlag[].class);

                    Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
                    buildFlag = (StateFlag) flagsClass.getField("BUILD").get(null);
                    pvpFlag = (StateFlag) flagsClass.getField("PVP").get(null);
                    exitFlag = (StateFlag) flagsClass.getField("EXIT").get(null);
                } catch (Exception ex) {
                    ChatUtils.error("Failed to bind to WorldGuard, integration will not work!");
                    regionContainer = null;
                    return;
                }
            } else {
                regionContainer = worldGuardPlugin.getRegionContainer();
                try {
                    createQueryMethod = regionContainer.getClass().getMethod("createQuery");
                    regionContainerGetMethod = regionContainer.getClass().getMethod("get", World.class);
                    Class<?> regionQueryClass = Class.forName("com.sk89q.worldguard.bukkit.RegionQuery");
                    regionQueryTestStateMethod = regionQueryClass.getMethod("testState", Location.class, RegionAssociable.class, StateFlag[].class);

                    Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag");
                    buildFlag = (StateFlag) flagsClass.getField("BUILD").get(null);
                    pvpFlag = (StateFlag) flagsClass.getField("PVP").get(null);
                    exitFlag = (StateFlag) flagsClass.getField("EXIT").get(null);
                } catch (Exception ex) {
                    ChatUtils.error("Failed to bind to WorldGuard, integration will not work!");
                    regionContainer = null;
                    return;
                }
            }

            // Ugh guys, API much?
            try {
                Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
                vectorConstructor = vectorClass.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
                regionManagerGetMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
            } catch (Exception ex) {
                try {
                    Class<?> vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                    vectorConstructorMethod = vectorClass.getMethod("at", Double.TYPE, Double.TYPE, Double.TYPE);
                    regionManagerGetMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
                } catch (Exception sodonewiththis) {
                    ChatUtils.error("Failed to bind to WorldGuard (no Vector class?), integration will not work!");
                    regionContainer = null;
                    return;
                }
            }

            if (regionContainer == null) {
                ChatUtils.error("Failed to find RegionContainer, WorldGuard integration will not function!");
            }
        }
    }

    private RegionManager getRegionManager(World world) {
        initialize();
        if (regionContainer == null || regionContainerGetMethod == null) return null;
        RegionManager regionManager = null;
        try {
            if (worldAdaptMethod != null) {
                Object worldEditWorld = worldAdaptMethod.invoke(null, world);
                regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer, worldEditWorld);
            } else {
                regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer, world);
            }
        } catch (Exception ex) {
            ChatUtils.error("An error occurred looking up a WorldGuard RegionManager");
        }
        return regionManager;
    }

    private ApplicableRegionSet getRegionSet(Location location) {
        RegionManager regionManager = getRegionManager(location.getWorld());
        if (regionManager == null) return null;
        // The Location version of this method is gone in 7.0
        // Oh and then they also randomly changed the Vector class at some point without even a version bump.
        // So awesome!
        try {
            Object vector = vectorConstructorMethod == null
                    ? vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ())
                    : vectorConstructorMethod.invoke(null, location.getX(), location.getY(), location.getZ());
            return (ApplicableRegionSet) regionManagerGetMethod.invoke(regionManager, vector);
        } catch (Exception ex) {
            ChatUtils.error("An error occurred looking up a WorldGuard ApplicableRegionSet");
        }
        return null;
    }
/*
    public boolean isInsideWGRegion(Location location) {
        ApplicableRegionSet rgset = getRegionSet(location);
        if (rgset == null || rgset.size() <= 0) return false;
        try {
            for (ProtectedRegion rg : rgset) {
                if (GenBucket.get().getConfig().getStringList("dont-consider-these-regions").stream().anyMatch(rg.getId()::equalsIgnoreCase))
                    continue;
                return true;
            }
        } catch (Exception ex) {
            return true;
        }
        return false;
    }
*/
    public boolean isPVPAllowed(Player player, Location location) {
        if (worldGuardPlugin == null || location == null) return true;

        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        return checkSet.queryState(getAssociable(player), pvpFlag) != StateFlag.State.DENY;
    }

    public boolean isExitAllowed(Player player, Location location) {
        if (worldGuardPlugin == null || location == null) return true;

        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        return checkSet.queryState(getAssociable(player), exitFlag) != StateFlag.State.DENY;
    }

    public boolean hasBuildPermission(Player player, Block block) {
        initialize();
        if (block != null && createQueryMethod != null && regionContainer != null) {
            try {
                boolean result;
                Object query = createQueryMethod.invoke(regionContainer);
                if (locationAdaptMethod != null) {
                    Object location = locationAdaptMethod.invoke(null, block.getLocation());
                    result = (boolean) regionQueryTestStateMethod.invoke(query, location, getAssociable(player), new StateFlag[]{buildFlag});
                } else {
                    result = (boolean) regionQueryTestStateMethod.invoke(query, block.getLocation(), getAssociable(player), new StateFlag[]{buildFlag});
                }
                return result;
            } catch (Exception ex) {
                ChatUtils.error("An error occurred querying WorldGuard");
            }
        }

        return true;
    }

}
