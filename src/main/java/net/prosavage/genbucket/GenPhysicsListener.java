package net.prosavage.genbucket;

import net.prosavage.genbucket.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class GenPhysicsListener implements Listener {

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        if (Config.ALLOW_GRAVITY_DOWN.getOption() && GenBucket.activeGenXZ.contains(event.getSourceBlock().getX() + ";" + event.getSourceBlock().getZ()))
            event.setCancelled(true);
    }
}
