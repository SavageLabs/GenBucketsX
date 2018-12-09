package net.prosavage.genbucket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.prosavage.genbucket.menu.impl.GenerationShopGUI;
import net.prosavage.genbucket.utils.MultiversionMaterials;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.prosavage.genbucket.file.CustomFile;
import net.prosavage.genbucket.file.impl.DataFile;
import net.prosavage.genbucket.file.impl.MessageFile;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.hooks.impl.VaultHook;

import net.milkbowl.vault.economy.Economy;

public class GenBucket extends JavaPlugin {

	private static GenBucket instance;
	public int taskID;
	public static Economy econ;
	public GenerationShopGUI generationShopGUI;
	private List<Material> materials = new ArrayList<Material>();
	
	public void onEnable() {
		(GenBucket.instance = this).saveDefaultConfig();
		getClass().isInstance(new HookManager(Arrays.asList(new FactionHook(), new VaultHook())));
		Arrays.asList(new MessageFile(), new DataFile()).forEach(CustomFile::init);
		getServer().getPluginManager().registerEvents(new GenListener(this), this);
		getConfig().getStringList("replace-blocks").forEach(s -> materials.add(MultiversionMaterials.valueOf(s).parseMaterial()));
		this.generationShopGUI = new GenerationShopGUI(this);
	}

	@Override
	public void onDisable() {
		Arrays.asList(new DataFile()).forEach(CustomFile::onExit);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("Genbucket") && sender instanceof Player) {
			((Player) sender).openInventory(generationShopGUI.init().getInventory());
		}
		return true;
	}

	public void start() {
		taskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, (Runnable) new GenListener(this), 0L, getConfig().getInt("delay"));
	}

	public void stop() {
		getServer().getScheduler().cancelTask(taskID);
	}

	public List<Material> getReplacements() {
		return materials;
	}

	public static GenBucket get() {
		return instance;
	}
}