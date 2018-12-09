package net.prosavage.genbucket.hooks.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultHook implements PluginHook<VaultHook> {
	
	@Override
	public VaultHook setup() {
		RegisteredServiceProvider<Economy> rsp = GenBucket.get().getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return null;
		}
		GenBucket.get().econ = rsp.getProvider();
		return this;
	}

	@Override
	public String getName() {
		return "Vault";
	}

}
