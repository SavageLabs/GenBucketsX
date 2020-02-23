package net.prosavage.genbucket;

import com.cryptomorin.xseries.XMaterial;
import net.milkbowl.vault.economy.Economy;
import net.prosavage.genbucket.command.GenBucketCommand;
import net.prosavage.genbucket.file.FileManager;
import net.prosavage.genbucket.file.impl.DataFile;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.menu.impl.GenerationShopGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class GenBucket extends JavaPlugin {

    public static Economy econ;
    private static GenBucket instance;

    public int taskID;

    public GenerationShopGUI generationShopGUI;

    public Set<Material> replaceBlocksWhiteList = new HashSet<>();
    public boolean replaceLiquids = false;

    private HookManager hookManager;
    private FileManager fileManager;

    public static GenBucket get() {
        return instance;
    }

    @Override
    public void onEnable() {
        (GenBucket.instance = this).saveDefaultConfig();
        checkServerVersion();
        this.getCommand("genbucket").setExecutor(new GenBucketCommand(this));

        this.hookManager = new HookManager(this);
        this.fileManager = new FileManager(this);

        getServer().getPluginManager().registerEvents(new GenListener(this), this);
        getConfig().getStringList("replace-blocks").forEach(s -> replaceBlocksWhiteList.add(XMaterial.valueOf(s).parseMaterial()));
        replaceLiquids = getConfig().getBoolean("replace-liquids", false);

        this.generationShopGUI = new GenerationShopGUI(this);
    }

    @Override
    public void onDisable() {
        DataFile dataFile = (DataFile) this.fileManager.getFileMap().get("data");
        dataFile.saveGenBuckets();
        instance = null;
        getServer().getScheduler().cancelTasks(this);
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
        return replaceBlocksWhiteList;
    }

    private static int ver;

    private void checkServerVersion() {
        ver = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].replace("1_", "").substring(1).replaceAll("_R\\d", ""));
    }

    public static int getServerVersion() {
        return ver;
    }

}