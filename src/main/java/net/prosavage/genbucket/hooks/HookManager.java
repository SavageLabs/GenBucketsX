package net.prosavage.genbucket.hooks;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.EssentialsHook;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.hooks.impl.VaultHook;
import net.prosavage.genbucket.hooks.impl.WorldGuardHook;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HookManager {

    private GenBucket plugin;
    private Map<String, PluginHook> pluginMap = new HashMap<>();

    public HookManager(GenBucket plugin) {
        this.plugin = plugin;
        hookPlugin(new WorldGuardHook());
        hookPlugin(new VaultHook());
        hookPlugin(new FactionHook());
        hookPlugin(new EssentialsHook());
    }

    private void hookPlugin(PluginHook pluginHook) {
        if (plugin.getServer().getPluginManager().getPlugin(pluginHook.getName()) == null) {
            plugin.getServer().getLogger().log(Level.WARNING, "Plugin failed to find " + pluginHook.getName()+", disabling hook");
            return;
        }
        pluginMap.put(pluginHook.getName(), (PluginHook<?>) pluginHook.setup(plugin));
    }

    public Map<String, PluginHook> getPluginMap() {
        return this.pluginMap;
    }

}
