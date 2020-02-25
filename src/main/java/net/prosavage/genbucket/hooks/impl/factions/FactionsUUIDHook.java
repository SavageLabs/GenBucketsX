package net.prosavage.genbucket.hooks.impl.factions;


import com.massivecraft.factions.*;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.perms.*;
import com.massivecraft.factions.util.FlightUtil;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class FactionsUUIDHook extends FactionHook {

    private Method playerCanBuildDestroyBlock = FactionsBlockListener.class.getMethod("playerCanBuildDestroyBlock", Player.class, Location.class, PermissibleAction.class, String.class, boolean.class);

    public FactionsUUIDHook() throws NoSuchMethodException {
        //Empty
    }

    @Override
    public boolean canBuild(Block block, Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!(boolean) playerCanBuildDestroyBlock.invoke(FactionsBlockListener.class, player, block.getLocation(), PermissibleAction.BUILD, "build", true)) {
            player.sendMessage(ChatUtils.color(Message.GEN_CANT_PLACE.getMessage()));
            return false;
        }
        return true;
    }

    @Override
    public boolean hasNearbyPlayer(Player player) {
        if (!GenBucket.get().getConfig().getBoolean("nearby-check")) {
            return false;
        }
        int radius = GenBucket.get().getConfig().getInt("radius");
        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (FlightUtil.instance().enemiesNearby(me,radius)) {
            player.sendMessage(ChatUtils.color(Message.GEN_ENEMY_NEARBY.getMessage()));
            return true;
        }
        return false;
    }

}