package net.prosavage.genbucket.api;

import net.prosavage.genbucket.gen.GenType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPlaceGenEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private GenType genType;
    private Location location;
    private Player player;

    public PlayerPlaceGenEvent(Player player, Location location, GenType genType) {
        this.player = player;
        this.location = location;
        this.genType = genType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public GenType getGenType() {
        return genType;
    }

}
