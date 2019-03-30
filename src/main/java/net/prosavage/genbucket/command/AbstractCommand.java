package net.prosavage.genbucket.command;

import net.prosavage.genbucket.GenBucket;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCommand implements Executable {

    private String label;
    public Set<String> alias = new HashSet<>();
    private boolean playerRequired;
    private GenBucket plugin;

    public AbstractCommand (GenBucket plugin, String label, boolean playerRequired) {
        this.label = label;
        this.playerRequired = playerRequired;
        this.plugin = plugin;
    }

    public String getLabel() {
        return label;
    }

    public boolean isPlayerRequired() {
        return playerRequired;
    }

    public GenBucket getPlugin() {
        return plugin;
    }
}