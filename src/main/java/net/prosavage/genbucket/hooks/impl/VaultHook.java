package net.prosavage.genbucket.hooks.impl;

import net.milkbowl.vault.economy.Economy;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements PluginHook<VaultHook> {

   @Override
   public VaultHook setup(GenBucket plugin) {
      RegisteredServiceProvider<Economy> rsp = GenBucket.get().getServer().getServicesManager().getRegistration(Economy.class);
      if (rsp == null) {
         return null;
      }
      GenBucket.econ = rsp.getProvider();
      return this;
   }

   @Override
   public String getName() {
      return "Vault";
   }

}
