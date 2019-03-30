package net.prosavage.genbucket.hooks;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.hooks.impl.VaultHook;
import net.prosavage.genbucket.hooks.impl.WorldGuardHook;
import net.prosavage.genbucket.utils.Logger;

import java.util.HashMap;
import java.util.List;
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
   }

   private void hookPlugin(PluginHook pluginHook) {
      if (plugin.getServer().getPluginManager().getPlugin(pluginHook.getName()) == null) {
         plugin.getServer().getLogger().log(Level.SEVERE, "Plugin failed to find " + pluginHook.getName());
         return;
      }
      pluginMap.put(pluginHook.getName(), (PluginHook<?>) pluginHook.setup(plugin));
   }

   public  Map<String, PluginHook> getPluginMap() {
      return this.pluginMap;
   }

}
