package net.prosavage.genbucket;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.WEST;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.gen.impl.HorizontalGen;
import net.prosavage.genbucket.gen.impl.VerticalGen;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ItemUtils;
import net.prosavage.genbucket.utils.Message;

import com.google.common.collect.ImmutableList;

public class GenListener implements Listener, Runnable {

	private GenBucket plugin;
	public static List<Generator> generations = new ArrayList<Generator>();
	private static final List<BlockFace> DIRECTIONS = ImmutableList.of(NORTH, EAST, SOUTH, WEST);

	public GenListener(GenBucket plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEmptyBucket(PlayerBucketEmptyEvent event) {
		ItemStack item = event.getPlayer().getItemInHand();
		if ((item.getType() == Material.LAVA_BUCKET || item.getType() == Material.WATER_BUCKET)&& ItemUtils.hasKey(item, "GENBUCKET")) {
			event.setCancelled(true);
			Block block = event.getBlockClicked().getRelative(event.getBlockFace());
			Player player = event.getPlayer();
			String name = ItemUtils.getKeyString(item, "GENBUCKET");
			Material mat = Material.valueOf(ItemUtils.getKeyString(item, "MATERIAL"));
				
			FactionHook facHook = ((FactionHook) HookManager.getPluginMap().get("Factions"));
			if (facHook.canBuild(block, player) && !facHook.hasNearbyPlayer(player)) {
				if (name.contains("VERTICAL") && withdraw(name + "." + mat.name(), event.getPlayer())) {
						register(new VerticalGen(plugin, event.getPlayer(), mat, block));
				} else if (name.contains("HORIZONTAL") && DIRECTIONS.contains(event.getBlockFace()) && withdraw(name + "." + mat.name(), event.getPlayer())) {
					register(new HorizontalGen(plugin, event.getPlayer(), mat, block, event.getBlockFace()));
				}
			}
		}
	}

	@EventHandler
	public void onPlaceBlock(PlayerInteractEvent event) {
		ItemStack item = event.getPlayer().getItemInHand();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !plugin.getConfig().getBoolean("use-bucket")) {
			if (item.hasItemMeta() && ItemUtils.hasKey(item, "GENBUCKET")) {
				event.setCancelled(true);
				
				Block block = event.getClickedBlock().getRelative(event.getBlockFace());
				Player player = event.getPlayer();
				String name = ItemUtils.getKeyString(item, "GENBUCKET");
				Material mat = Material.valueOf(ItemUtils.getKeyString(item, "MATERIAL"));
				
				FactionHook facHook = (FactionHook) HookManager.getPluginMap().get("Factions");
				if (facHook.canBuild(block, player) && !facHook.hasNearbyPlayer(player)) {
					if (name.contains("VERTICAL") && withdraw(name + "." + mat.name(), event.getPlayer())) {
						register(new VerticalGen(plugin, event.getPlayer(), mat, block));
					} else if (name.contains("HORIZONTAL") && DIRECTIONS.contains(event.getBlockFace()) && withdraw(name + "." + mat.name(), event.getPlayer())) {
						register(new HorizontalGen(plugin, event.getPlayer(), mat, block, event.getBlockFace()));
					}
				}
			}
		}
	}

	@EventHandler
	public void onDropItem(ItemSpawnEvent event) {
		ItemStack item = event.getEntity().getItemStack();
		if (ItemUtils.hasKey(item, "GENBUCKET")) {
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (event.getInventory().getName().equals(plugin.generationShopGUI.getTitle()) && event.getClickedInventory() != player.getInventory()) {
			if (event.getCurrentItem() != null && event.getInventory() != null && ItemUtils.hasKey(event.getCurrentItem(), "GENBUCKET")) {
				ItemStack item = event.getCurrentItem().clone();
				if (item != null && item.getType() != Material.LAVA_BUCKET) {
					if (plugin.getConfig().getBoolean("use-bucket")) {
						item.setType(Material.LAVA_BUCKET);
					} else {
						item.setAmount(64);
					}
				}
				if (!player.getInventory().contains(item)) {
					player.getInventory().addItem(item);
				} else {
					player.sendMessage(Message.GEN_HAS_ALREADY.getMessage());
				}
			}
			event.setCancelled(true);
		}
	}

	public void run() {
		if (generations.size() != 0) {
			for (Iterator<Generator> iterator = generations.iterator(); iterator.hasNext();) {
				Generator generator = iterator.next();
				if (generator.isFinished()) {
					iterator.remove();
					continue;
				}
				generator.run();
			}
		} else {
			GenBucket.get().stop();
			generations.clear();
		}
	}

	public void register(Generator generator) {
		if (generations.size() == 0) {
			GenBucket.get().start();
		}
		generations.add(generator);
	}

	public boolean withdraw(String type, Player player) {
		int price = plugin.getConfig().getInt(type + ".price");
		if (GenBucket.econ.withdrawPlayer(player, price).transactionSuccess()) {
			return true;
		}
		player.sendMessage(Message.GEN_CANT_AFFORD.getMessage());
		return false;
	}

}
