package net.prosavage.genbucket.gen.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.MultiversionMaterials;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class HorizontalGen extends Generator {

	private BlockFace blockFace;
	
	public HorizontalGen(GenBucket plugin, Player player, Material material, Block block, BlockFace blockFace) {
		super(plugin, player, material, block, GenType.HORIZONTAL);
		this.blockFace = blockFace;
		if (isValidLocation(block)) {
			block.setType(MultiversionMaterials.GREEN_WOOL.parseMaterial());
		} else {
			player.sendMessage(Message.GEN_CANT_PLACE.getMessage());
		}
	}

	public HorizontalGen(String data) {
		super(GenBucket.get(), null, Material.valueOf(data.split(",")[0]), getBlockfromString(data.split(",")[1]), GenType.VERTICAL);
		setIndex(Integer.valueOf(data.split(",")[2]));
		blockFace = BlockFace.valueOf(data.split(",")[3]);
		setData(true);
	}
	
	public void run() {
		if (isDataGen() || ((FactionHook) HookManager.getPluginMap().get("Factions")).canBuild(getBlock(), getPlayer())) {
			setIndex(getIndex() + 1);
			Block gen = getBlock().getWorld().getBlockAt(getBlock().getX() + getIndex() * blockFace.getModX(), getBlock().getY(), getBlock().getZ() +  getIndex() * blockFace.getModZ());
			
			getBlock().getChunk().load();
			gen.getChunk().load();
			
			if (!isDataGen() && !isValidLocation(gen)) {
				getBlock().setType(getMaterial());
				setFinished(true);
				return;
			}
			
			if(getPlayer() == null || !((FactionHook) HookManager.getPluginMap().get("Factions")).canBuild(gen, getPlayer())) {
				getBlock().setType(getMaterial());
				setFinished(true);
				return;
			}
			
			if(getBlock().getType() != MultiversionMaterials.GREEN_WOOL.parseMaterial() && getPlayer() != null) {
				getPlayer().sendMessage(Message.GEN_CANCELLED.getMessage());
				getBlock().setType(getMaterial());
				setFinished(true);
				return;
			}

			if (getIndex() < getPlugin().getConfig().getInt("distance")) {
				gen.setType(getMaterial());
			} else {
				getBlock().setType(getMaterial());
				setFinished(true);
			}
		} 
	}

	@Override	
	public String toString() {
		return this.getMaterial() + "," + getLocation(this.getBlock().getLocation()) + "," + getIndex() + "," + blockFace.name();
	}
	
	public String getLocation(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	}

}
