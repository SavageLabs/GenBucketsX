package net.prosavage.genbucket.hooks.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.utils.Logger;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import net.prosavage.genbucket.hooks.PluginHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionMCHook;
import net.prosavage.genbucket.hooks.impl.factions.FactionUUIDHook;

import java.util.List;

public class FactionHook implements PluginHook<FactionHook> {

	@Override
	public FactionHook setup() {
		if (GenBucket.get().getServer().getPluginManager().getPlugin(getName()) == null) {
			Logger.print("Factions could not be found", Logger.PrefixType.WARNING);
			return null;
		}
		List<String> authors = GenBucket.get().getServer().getPluginManager().getPlugin(getName()).getDescription().getAuthors();
		if (!authors.contains("drtshock") && !authors.contains("Benzimmer")) {
			Logger.print("Server Factions type has been set to (MassiveCore)", Logger.PrefixType.DEFAULT);
			return new FactionMCHook();
		} else {
			Logger.print("Server Factions type has been set to (FactionsUUID/SavageFactions/FactionsUltimate)", Logger.PrefixType.DEFAULT);
			return new FactionUUIDHook();
		}
	}

	public boolean canBuild(Block block, Player player) {
		throw new NotImplementedException("Factions does not exist!");
	}

	public boolean hasNearbyPlayer(Player player) {
		throw new NotImplementedException("Factions does not exist!");
	}

	@Override
	public String getName() {
		return "Factions";
	}

}
