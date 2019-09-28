package net.prosavage.genbucket;

import net.milkbowl.vault.economy.Economy;
import net.prosavage.genbucket.command.GenBucketCommand;
import net.prosavage.genbucket.file.FileManager;
import net.prosavage.genbucket.file.impl.DataFile;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.menu.impl.GenerationShopGUI;
import net.prosavage.genbucket.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class GenBucket extends JavaPlugin {

    public static Economy econ;
    private static GenBucket instance;

    public int taskID;

    public GenerationShopGUI generationShopGUI;

    public Set<Material> materials = new HashSet<>();

    private HookManager hookManager;
    private FileManager fileManager;

    public static GenBucket get() {
        return instance;
    }

    public void onEnable() {
        (GenBucket.instance = this).saveDefaultConfig();
        this.getCommand("genbucket").setExecutor(new GenBucketCommand(this));

        this.hookManager = new HookManager(this);
        this.fileManager = new FileManager(this);

        getServer().getPluginManager().registerEvents(new GenListener(this), this);
        getConfig().getStringList("replace-blocks").forEach(s -> materials.add(XMaterial.valueOf(s).parseMaterial()));

        this.generationShopGUI = new GenerationShopGUI(this);
    }

    @Override
    public void onDisable() {
        DataFile dataFile = (DataFile) this.fileManager.getFileMap().get("data");
        dataFile.saveGenBuckets();
    }

    public void start() {
        taskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new GenListener(this), 0L, getConfig().getInt("delay"));
    }

    public void stop() {
        getServer().getScheduler().cancelTask(taskID);
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public Set<Material> getReplacements() {
        return materials;
    }
}