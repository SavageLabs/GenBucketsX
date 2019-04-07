package net.prosavage.genbucket.hooks.impl.worldguard;


import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.prosavage.genbucket.hooks.impl.WorldGuardHook;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class WorldGuard6 extends WorldGuardHook {

    private Method method;
    {
        try {
            method = WorldGuardPlugin.class.getMethod("canBuild", Player.class, Block.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canBuild(Player player, Block block) {
        try {
            return (boolean) method.invoke(WorldGuardPlugin.inst(), player, block);
        } catch (Exception e) {
            return false;
        }
    }

}
