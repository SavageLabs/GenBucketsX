package net.prosavage.genbucket.hooks;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.*;
import net.prosavage.genbucket.utils.Message;

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
        hookPlugin(new CoreProtectHook());
    }

    private void hookPlugin(PluginHook pluginHook) {
        String[] pName;
        if (pluginHook.getName().contains(",")) {
            pName = pluginHook.getName().split(",");
        } else {
            pName = new String[]{pluginHook.getName()};
        }
        for (String hookName : pName) {
            if (plugin.getServer().getPluginManager().getPlugin(hookName) == null) {
                plugin.getServer().getLogger().log(Level.WARNING, Message.ERROR_HOOK_NOTFOUND.getMessage().replace("%plugin%", hookName));
                return;
            }
            pluginMap.put(hookName, (PluginHook<?>) pluginHook.setup(plugin));
        }
    }

    public Map<String, PluginHook> getPluginMap() {
        return this.pluginMap;
    }

}
