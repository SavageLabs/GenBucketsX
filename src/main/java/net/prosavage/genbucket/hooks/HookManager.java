package net.prosavage.genbucket.hooks;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.*;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;

import java.util.HashMap;
import java.util.Map;

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
        if (!pluginHook.getName().equalsIgnoreCase("Factions") && plugin.getServer().getPluginManager().getPlugin(pluginHook.getName()) == null) {
            ChatUtils.sendConsole(Message.ERROR_HOOK_NOTFOUND.getMessage().replace("%plugin%", pluginHook.getName()));
            return;
        }
        pluginMap.put(pluginHook.getName(), (PluginHook<?>) pluginHook.setup(plugin));
    }

    public Map<String, PluginHook> getPluginMap() {
        return this.pluginMap;
    }

}
