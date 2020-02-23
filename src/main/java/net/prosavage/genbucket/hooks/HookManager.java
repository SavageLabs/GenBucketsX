package net.prosavage.genbucket.hooks;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.*;

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
        hookPlugin(new SuperVanishHook());
    }

    private void hookPlugin(PluginHook pluginHook) {
        String[] pName;
        if (pluginHook.getName().contains(",")) {
            pName = pluginHook.getName().split(",");
        } else {
            pName = new String[] {pluginHook.getName()};
        }
        for (String hookName : pName) {
            if (plugin.getServer().getPluginManager().getPlugin(hookName) == null) {
                plugin.getServer().getLogger().log(Level.WARNING, "Plugin failed to find " + hookName + ", disabling hook");
                return;
            }
            pluginMap.put(hookName, (PluginHook<?>) pluginHook.setup(plugin));
        }
    }

    public Map<String, PluginHook> getPluginMap() {
        return this.pluginMap;
    }

}
