package net.prosavage.genbucket.gen.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.MultiversionMaterials;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;



public class VerticalGen extends Generator {

	public VerticalGen(GenBucket plugin, Player player, Material material, Block block) {
		super(plugin, player, material, block, GenType.VERTICAL);
		setIndex(material.hasGravity() ? 1 : -1);
		if (isValidLocation(block)) {
			block.setType(MultiversionMaterials.valueOf(getPlugin().getConfig().getString("sourceblock.item-name")).parseMaterial());
		} else {
			player.sendMessage(Message.GEN_CANT_PLACE.getMessage());
		}
	}

	public VerticalGen(String data) {
		super(GenBucket.get(), null, Material.valueOf(data.split(",")[0]), getBlockfromString(data.split(",")[1]), GenType.VERTICAL);
		setIndex(Integer.valueOf(data.split(",")[2]));
		setData(true);
	}

	public void run() {
		Block gen = getBlock().getWorld().getBlockAt(getBlock().getX(), getBlock().getY() + getIndex(), getBlock().getZ());
		setIndex(getIndex() + (getMaterial().hasGravity() ? 1 : -1));
		getBlock().getChunk().load();

		if (!isDataGen() && !isValidLocation(gen)) {
			getBlock().setType(getMaterial());
			setFinished(true);
			return;
		}

		if (getBlock().getType() != MultiversionMaterials.valueOf(getPlugin().getConfig().getString("sourceblock.item-name")).parseMaterial() && getPlayer() != null) {
			getPlayer().sendMessage(Message.GEN_CANCELLED.getMessage());
			getBlock().setType(getMaterial());
			setFinished(true);
			return;
		}

		if (!isNearSponge(gen, 3) && (getBlock().getY() + getIndex()) >= 0 && (getBlock().getY() + getIndex()) < 256) {
			gen.setType(getMaterial());
		} else {
			getBlock().setType(getMaterial());
			setFinished(true);
		}
	}

	public boolean isNearSponge(Block block, int radius) {
		if (!GenBucket.get().getConfig().getBoolean("sponge-check")) {
			return false;
		}
		for (int x = block.getX() - radius; x <= block.getX() + radius; x++) {
			for (int z = block.getZ() - radius; z <= block.getZ() + radius; z++) {
				if (block.getWorld().getBlockAt(x, block.getY() - radius, z).getType() == Material.SPONGE)
					return true;
			}
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		return this.getMaterial() + "," + getLocation(this.getBlock().getLocation()) + "," + getIndex();
	}

	public String getLocation(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	}

}
